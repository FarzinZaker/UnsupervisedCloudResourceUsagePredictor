package f3

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
//import weka.classifiers.functions.LinearRegression
//import weka.classifiers.functions.SGD
//import weka.classifiers.lazy.IBk
//import weka.classifiers.meta.MultiClassClassifierUpdateable
//import weka.core.Attribute
//import weka.core.DenseInstance
//import weka.core.Instances
//import weka.core.SerializationHelper

@Transactional
class ModelService {

    GrailsApplication grailsApplication

    public final static INPUT_LENGTH = 10000

//    private MultiClassClassifierUpdateable _model
//    private Instances _structure

//    private Instances getStructure() {
//        if (!_structure) {
//            def attributes = new ArrayList<Attribute>()
//            INPUT_LENGTH.times { index ->
//                attributes.add(index, new Attribute("Attribute_${index + 1}"))
//            }
//            _structure = new Instances('F3Model', attributes, 9999999)
//            _structure.setClassIndex(_structure.numAttributes() - 1)
//        }
//        _structure
//    }

//    MultiClassClassifierUpdateable getModel() {
//        if (!_model) {
//            def path = grailsApplication.config["model.path"]?.toString()
//            if (new File(path).exists())
//                _model = (MultiClassClassifierUpdateable) SerializationHelper.read(path)
//            else {
//                _model = new MultiClassClassifierUpdateable()
//                _model.setClassifier(new IBk())
//                _model.buildClassifier(structure)
//                saveModel()
//                _model = (MultiClassClassifierUpdateable) SerializationHelper.read(path)
//            }
//        }
//
//        _model
//    }

//    void saveModel() {
//        def path = grailsApplication.config["model.path"]?.toString()
//        SerializationHelper.write(path, model)
//        _model = null
//    }

    void train(List<Map> inputs) {

//        10000.times {
//            def instance = new DenseInstance(INPUT_LENGTH)
//            structure.setClassIndex(it)
//            instance.setDataset(structure)
//
//            10000.times {
//                instance.setValue(it, 0D)
//            }
//
//            inputs?.each {
//                instance.setValue((it.index as Integer) - 1, it.value as Double)
//            }
//
//            model.updateClassifier(instance)
//        }
//        saveModel()
    }

    Integer predictNext(List<Map> inputs) {

//        def instance = new DenseInstance(INPUT_LENGTH)
//        instance.setDataset(structure)
//
//        10000.times {
//            instance.setValue(it, 0D)
//        }
//
//        inputs?.each {
//            instance.setValue((it.index as Integer) - 1, it.value as Double)
//        }
//
//        try {
//            model.classifyInstance(instance)
//        } catch (ignored) {
//            1
//        }
    }
}
