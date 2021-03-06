package f3

import grails.gorm.transactions.Transactional
import org.datavec.api.records.reader.SequenceRecordReader
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader
import org.datavec.api.split.NumberedFileInputSplit
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator
import org.deeplearning4j.exception.DL4JInvalidInputException
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.LSTM
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.activations.Activation

//import org.nd4j.linalg.cpu.nativecpu.NDArray
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Nadam
import org.nd4j.linalg.lossfunctions.LossFunctions

import java.text.DecimalFormat

@Transactional
class DLModelService {

    def grailsApplication


    private MultiLayerNetwork _net
    private NormalizerStandardize _normalizer

    MultiLayerNetwork getNet() {
        if (!_net) {
            def path = grailsApplication.config["model.path"]?.toString()
            _net = (MultiLayerNetwork) ModelSerializer.restoreMultiLayerNetwork(path, true)
        }
        _net
    }

    def saveModel(MultiLayerNetwork net) {
        def path = grailsApplication.config["model.path"]?.toString()
        ModelSerializer.writeModel(net, path, true)
        _net = null
    }

    NormalizerStandardize getNormalizer() {
        if (!_normalizer) {
            def path = grailsApplication.config["model.normalizer"]?.toString()
            _normalizer = (NormalizerStandardize) NormalizerSerializer.getDefault().restore(path)
        }
        _normalizer
    }

    def saveNormalizer(NormalizerStandardize normalizer) {
        def path = grailsApplication.config["model.normalizer"]?.toString()
        NormalizerSerializer.getDefault().write(normalizer, path)
        _normalizer = null
    }

    void retrain() {

        dataDir = new File(grailsApplication.config["model.dataFiles"]?.toString())
        featuresDir = new File(dataDir, "features")

        int miniBatchSize = 10
        int numLabelClasses = 6

        // ----- Load the training data -----
        SequenceRecordReader trainFeatures = new CSVSequenceRecordReader(1, ",")
        trainFeatures.initialize(new NumberedFileInputSplit(featuresDir.getAbsolutePath() + "/%d.csv", 0, featuresDir.listFiles().count {
            it.name.endsWith('.csv')
        }.toInteger() - 1))
        SequenceRecordReader trainLabels = new CSVSequenceRecordReader(1, ",")
        trainLabels.initialize(new NumberedFileInputSplit(featuresDir.getAbsolutePath() + "/%d.csv", 0, featuresDir.listFiles().count {
            it.name.endsWith('.csv')
        }.toInteger() - 1))

        DataSetIterator trainData = new SequenceRecordReaderDataSetIterator(trainFeatures, trainLabels, miniBatchSize, numLabelClasses,
                true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END)

        //Normalize the training data
        NormalizerStandardize normalizer = new NormalizerStandardize()
        normalizer.fit(trainData)
        trainData.reset()

        trainData.setPreProcessor(normalizer)

        def nIn = new File(featuresDir, "0.csv").readLines().find().split(',').size()
        def nOut = nIn

        // ----- Configure the network -----
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Nadam())
                .list()
                .layer(new LSTM.Builder().activation(Activation.TANH).nIn(nIn).nOut(nOut).build())
                .layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX).nIn(nIn).nOut(nOut).build())
                .build()

        MultiLayerNetwork net = new MultiLayerNetwork(conf)
        net.init()

        int nEpochs = 40
        net.fit(trainData, nEpochs)

        saveModel(net)
        saveNormalizer(normalizer)

        def trainedFlag = new File(dataDir, "trained")
        if (!trainedFlag.exists())
            trainedFlag.createNewFile()

        println "Model Retrained"
    }

    void train() {
        dataDir = new File(grailsApplication.config["model.dataFiles"]?.toString())
        def nIn = new File(dataDir, "latest.csv").readLines().find().split(',').size()
        def latestDataAll = new File(dataDir, 'latest.csv').text.trim().split('\n')
        def latestData = latestDataAll[1].split(',')
        def values = latestData.collect {
            it.toDouble()
        }.toArray() as Double[]
        def input = Nd4j.create(values)
        def record = Nd4j.zeros(1, nIn)
        for (def i = 0; i < input.length(); i++)
            record.put(0, i, input[i] as double)
        net.output(record, true)

        saveModel(net)

        def trainedFlag = new File(dataDir, "trained")
        if (!trainedFlag.exists())
            trainedFlag.createNewFile()

        println "Model Trained"
    }

    List<Map> safePredict() {
        try {
            predict()
        }
        catch (DL4JInvalidInputException ignored) {
            println ignored.message
            retrain()
            predict()
        }
    }

    List<Map> predict() {
        dataDir = new File(grailsApplication.config["model.dataFiles"]?.toString())
        def nIn = new File(dataDir, "latest.csv").readLines().find().split(',').size()
        def latestDataAll = new File(dataDir, 'latest.csv').text.trim().split('\n')
        def headers = latestDataAll[0].split(',').collect {
            def parts = it.split('_')
            [
                    instance: parts[0],
                    metric  : parts[1]
            ]
        }
        def latestData = latestDataAll[1].split(',')
        def values = latestData.collect {
            it.toDouble()
        }.toArray() as Double[]
        def input = Nd4j.create(values)
        def record = Nd4j.zeros(1, nIn)
        for (def i = 0; i < input.length(); i++)
            record.put(0, i, input[i] as double)

        def output = net.rnnTimeStep(record)
        normalizer.revertFeatures(output)
        output = output.toDoubleVector()

        def result = []
        for (def i = 0; i < latestData.size(); i++)
            result << [resource: headers[i].instance, metric: headers[i].metric, current: latestData[i], next: df.format(output[i])]
        result.sort { it.metric }
        result.sort { it.resource }
        result
    }

    File dataDir
    File featuresDir
    DecimalFormat df = new DecimalFormat("0.##")

    File createNewFile(File parent = null, String index) {
        def file = new File(parent ?: featuresDir, "${index}.csv")
        if (file.exists())
            file.delete()
        file.createNewFile()
        file.write(MetricOrder.createCriteria().list {
            order('id')
        }.collect { "${it.instance}_${it.name}" }.join(','))
        file
    }

    void addAdditionalMetrics() {
        def metrics = MetricOrder.createCriteria().list { order('id') }.collect { "${it.instance}_${it.name}" }
        def newMetricAdded = true
        featuresDir.eachFile { file ->
            if (newMetricAdded) {
                def lines = file.readLines()
                def existingMetrics = (lines.find() ?: '').split(',')
                def newMetrics = metrics.findAll { !existingMetrics.contains(it) }
                if (newMetrics.size()) {
                    for (def i = 0; i < lines.size(); i++) {
                        if (lines[i].trim() != '')
                            lines[i] += ','
                        if (i == 0)
                            lines[i] += newMetrics.join(',')
                        else
                            lines[i] += newMetrics.collect { df.format(0) }.join(',')

                    }
                    def metricsCount = lines[0].split(',').size()
                    println metricsCount
                    file.write(lines.collect { it.split(',')[0..(metricsCount - 1)].join(',') }.join('\n'))
                } else
                    newMetricAdded = false
            }
        }
    }

    void prepareTrainingData(List<Metric> inputs) {

        dataDir = new File(grailsApplication.config["model.dataFiles"]?.toString())
        featuresDir = new File(dataDir, "features")

        dataDir.mkdir()
        featuresDir.mkdir()

        def files = featuresDir.listFiles().sort { it.name.replace('.csv', '').toInteger() }
        if (!files?.size()) {
            createNewFile(0)
            files = featuresDir.listFiles().sort { it.name.replace('.csv', '').toInteger() }
        }
        def currentFile = files.last()
        if (currentFile.length() > 200 * 1024) {
            createNewFile((currentFile.name.split('/').last().replace('.csv', '').toInteger() + 1).toString())
            files = featuresDir.listFiles().sort { it.name.replace('.csv', '').toInteger() }
            currentFile = files.last()
        }

        addAdditionalMetrics()

        def currentData = inputs.sort {
            def order = MetricOrder.findByInstanceAndName(it.instance, it.name)
            if (!order)
                order = new MetricOrder(instance: it.instance, name: it.name).save(flush: true)
            order.id
        }.collect {
            df.format(it.value)
        }
        currentFile.append('\n' + currentData.join(','))

        //def latestFile = new File(dataDir, "latest.csv")
        def latestFile = createNewFile(dataDir, 'latest')

        latestFile.append('\n' + currentData.join(','))
    }

//    void prepareTrainingData(List<Map> inputs) {
//
//        File dataDir = new File(grailsApplication.config["model.dataFiles"]?.toString())
//        File featuresDir = new File(dataDir, "features")
//        File labelsDir = new File(dataDir, "labels")
//
//        dataDir.mkdir()
//        featuresDir.mkdir()
//        labelsDir.mkdir()
//
//        def fileNumbers = [:]
//
//        List<Pair<Double, String>> contentAndLabels = new ArrayList<>()
//        for (Map input : inputs) {
//            contentAndLabels.add(new Pair<>(input.value as Double, "${input.instance}_${input.metric}".toString()))
//            fileNumbers.put("${input.instance}_${input.metric}".toString(), input.index)
//        }
//
//        DecimalFormat df = new DecimalFormat("#.00")
//
//        for (Pair<Double, String> p : contentAndLabels) {
//
//            def fileNumber = fileNumbers[p.value]
//            File outPathFeatures
//            File outPathLabels
//            outPathFeatures = new File(featuresDir, fileNumber + ".csv")
//            if (!outPathFeatures.exists())
//                outPathFeatures.createNewFile()
//            outPathLabels = new File(labelsDir, fileNumber + ".csv")
//            if (!outPathLabels.exists())
//                outPathLabels.createNewFile()
//
//            if (outPathFeatures.text && outPathFeatures.text?.trim() != '')
//                FileUtils.writeStringToFile(outPathFeatures, '\n', (Charset) null, true)
//            FileUtils.writeStringToFile(outPathFeatures, df.format(p.getFirst()), (Charset) null, true)
//
//            if (!outPathLabels.text || outPathLabels.text?.trim() == '')
//                FileUtils.writeStringToFile(outPathLabels, p.getSecond().toString(), (Charset) null, true)
//        }
//    }
}