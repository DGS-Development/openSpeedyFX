package de.dgs.apps.openspeedyfx.game.mapinfo;

import java.util.List;

public class MapInfo {
    /*
    public static class Coordinate {
        private double x;
        private double y;

        public Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
    */

    public static class FieldInfo {
        private String fieldId;
        private String fieldType;

        public FieldInfo(String fieldId, String fieldType) {
            this.fieldId = fieldId;
            this.fieldType = fieldType;
        }

        public String getFieldId() {
            return fieldId;
        }

        public String getFieldType() {
            return fieldType;
        }
    }

    public static class PathInfo {
        private FieldInfo startFieldInfo;
        private FieldInfo endFieldInfo;

        private double controlX;
        private double controlY;

        public PathInfo(FieldInfo startFieldInfo, FieldInfo endFieldInfo, double controlX, double controlY) {
            this.startFieldInfo = startFieldInfo;
            this.endFieldInfo = endFieldInfo;
            this.controlX = controlX;
            this.controlY = controlY;
        }

        public FieldInfo getStartFieldInfo() {
            return startFieldInfo;
        }

        public FieldInfo getEndFieldInfo() {
            return endFieldInfo;
        }

        public double getControlX() {
            return controlX;
        }

        public double getControlY() {
            return controlY;
        }
    }

    private String mapName;
    private boolean isCompetitive;
    private String fxmlName;
    private List<PathInfo> pathInfos;

    public MapInfo() {
        //Ignore...
    }

    public MapInfo(String mapName, boolean isCompetitive, String fxmlName, List<PathInfo> pathInfos) {
        this.mapName = mapName;
        this.isCompetitive = isCompetitive;
        this.fxmlName = fxmlName;
        this.pathInfos = pathInfos;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public boolean isCompetitive() {
        return isCompetitive;
    }

    public void setCompetitive(boolean competitive) {
        isCompetitive = competitive;
    }

    public String getFxmlName() {
        return fxmlName;
    }

    public void setFxmlName(String fxmlName) {
        this.fxmlName = fxmlName;
    }

    public List<PathInfo> getPaths() {
        return pathInfos;
    }

    public void setPaths(List<PathInfo> pathInfos) {
        this.pathInfos = pathInfos;
    }
}
