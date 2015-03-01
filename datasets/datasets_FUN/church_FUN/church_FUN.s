[General]
Compatibility = MLJ08

[Data]
File = church_FUN.train.arff
PruneSet = church_FUN.valid.arff
TestSet = church_FUN.test.arff

[Attributes]
ReduceMemoryNominalAttrs = yes

[Hierarchical]
Type = TREE
WType = ExpAvgParentWeight
HSeparator = /
%ClassificationThreshold = [20,50,80]
%SingleLabel = yes

[Tree]
Optimize = {NoClusteringStats, NoINodeStats}
ConvertToRules = No
FTest = [0.001,0.005,0.01,0.05,0.1,0.125]
%FTest = 0.001

[Model]
MinimalWeight = 1.0

[Output]
%TrainErrors = No
%ValidErrors = No
%ShowModels = {Original}
WritePredictions = Test
