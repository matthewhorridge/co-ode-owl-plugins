Stand-alone Java segmentation application that segments any given ontology.
Example usage:

java -Xmx1500m -jar SegmentationApp.jar full-galen.owl Heart Liver BloodPressure

java -Xmx1500m -jar SegmentationApp.jar full-galen.owl list_of_targets.txt
(where list_of_targets.txt contains one classname per line)