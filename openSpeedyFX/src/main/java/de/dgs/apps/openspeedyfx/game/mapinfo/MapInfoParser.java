package de.dgs.apps.openspeedyfx.game.mapinfo;

import de.dgs.apps.openspeedyfx.game.logic.model.Tile;
import de.dgs.apps.openspeedyfx.game.logic.model.TileType;
import de.dgs.apps.openspeedyfx.speedyfield.SpeedyFxField;
import de.dgs.apps.openspeedyfx.speedyfield.SpeedyFxFieldTypes;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.*;

public class MapInfoParser {
    public static class MapData {
        private Pane rootNode;
        private Map<SpeedyFxField, List<SpeedyFxField>> fieldsMapping;
        private Map<Tile, List<Tile>> tilesMapping;
        private Tile firstTile;
        private Tile startTile;
        private Map<SpeedyFxField, Tile> fieldTileMapping;
        private Map<Tile, SpeedyFxField> tileFieldMapping;
        private List<Tile> tiles;
        private List<SpeedyFxField> speedyFxFields;
        private MapInfo mapInfo;

        public Pane getRootNode() {
            return rootNode;
        }

        public void setRootNode(Pane rootNode) {
            this.rootNode = rootNode;
        }

        public Map<SpeedyFxField, List<SpeedyFxField>> getFieldsMapping() {
            return fieldsMapping;
        }

        public void setFieldsMapping(Map<SpeedyFxField, List<SpeedyFxField>> fieldsMapping) {
            this.fieldsMapping = fieldsMapping;
        }

        public Map<Tile, List<Tile>> getTilesMapping() {
            return tilesMapping;
        }

        public void setTilesMapping(Map<Tile, List<Tile>> tilesMapping) {
            this.tilesMapping = tilesMapping;
        }

        public Tile getFirstTile() {
            return firstTile;
        }

        public void setFirstTile(Tile firstTile) {
            this.firstTile = firstTile;
        }

        public Tile getStartTile() {
            return startTile;
        }

        public void setStartTile(Tile startTile) {
            this.startTile = startTile;
        }

        public Map<SpeedyFxField, Tile> getFieldTileMapping() {
            return fieldTileMapping;
        }

        public void setFieldTileMapping(Map<SpeedyFxField, Tile> fieldTileMapping) {
            this.fieldTileMapping = fieldTileMapping;
        }

        public Map<Tile, SpeedyFxField> getTileFieldMapping() {
            return tileFieldMapping;
        }

        public void setTileFieldMapping(Map<Tile, SpeedyFxField> tileFieldMapping) {
            this.tileFieldMapping = tileFieldMapping;
        }

        public List<SpeedyFxField> getSpeedyFxFields() {
            return speedyFxFields;
        }

        public void setSpeedyFxFields(List<SpeedyFxField> speedyFxFields) {
            this.speedyFxFields = speedyFxFields;
        }

        public List<Tile> getTiles() {
            return tiles;
        }

        public void setTiles(List<Tile> tiles) {
            this.tiles = tiles;
        }

        public MapInfo getMapInfo() {
            return mapInfo;
        }

        public void setMapInfo(MapInfo mapInfo) {
            this.mapInfo = mapInfo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MapData that = (MapData) o;
            return rootNode.equals(that.rootNode) && fieldsMapping.equals(that.fieldsMapping) && tilesMapping.equals(that.tilesMapping) &&
                    firstTile.equals(that.firstTile) && startTile.equals(that.startTile) && fieldTileMapping.equals(that.fieldTileMapping) &&
                    tileFieldMapping.equals(that.tileFieldMapping) && speedyFxFields.equals(that.speedyFxFields) &&
                    tiles.equals(that.tiles) && mapInfo.equals(that.mapInfo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rootNode, fieldsMapping, tilesMapping, firstTile, startTile, fieldTileMapping,
                    tileFieldMapping, speedyFxFields, tiles, mapInfo);
        }
    }

    public static MapData createMapData(MapInfo mapInfo, Pane rootNode) throws Exception {
        //Find available fields.
        List<SpeedyFxField> speedyFxFields = readAvailableSpeedyFxFields(rootNode);

        if(speedyFxFields.isEmpty())
            throw new Exception("Unable to create map data: No fields (" + SpeedyFxField.class.getSimpleName() + ") were found.");

        //Replace random fields and create corresponding tiles.
        List<Tile> tiles = new LinkedList<>();
        Map<SpeedyFxField, Tile> fieldTileMapping = new HashMap<>();
        Map<Tile, SpeedyFxField> tileFieldMapping = new HashMap<>();

        SpeedyFxField hedgehogStartField = null;

        for(SpeedyFxField tmpSpeedyFxField : speedyFxFields) {
            if(tmpSpeedyFxField.getFxFieldType() == SpeedyFxFieldTypes.RANDOM_ITEM_FIELD)
                tmpSpeedyFxField.setFxFieldType(createRandomFieldType());

            if(tmpSpeedyFxField.getFxFieldType() == SpeedyFxFieldTypes.START_FIELD)
                hedgehogStartField = tmpSpeedyFxField;

            Tile correspondingTile = new Tile(fieldTypeToTileType(tmpSpeedyFxField.getFxFieldType()));

            tiles.add(correspondingTile);
            fieldTileMapping.put(tmpSpeedyFxField, correspondingTile);
            tileFieldMapping.put(correspondingTile, tmpSpeedyFxField);
        }

        if(hedgehogStartField == null)
            throw new Exception("Unable to find hedgehog start (" + SpeedyFxField.class.getSimpleName() + ").");

        //Create mappings from map info (paths).
        Map<SpeedyFxField, List<SpeedyFxField>> fieldsMapping = new HashMap<>();
        Map<Tile, List<Tile>> tilesMapping = new HashMap<>();

        for(MapInfo.PathInfo tmpPathInfo : mapInfo.getPaths()) {
            //Find start field.
            SpeedyFxField startField = findSpeedyFxField(speedyFxFields, tmpPathInfo.getStartFieldInfo().getFieldId());

            if(startField == null)
                throw new Exception("Unable to find " + SpeedyFxField.class.getSimpleName() + " with id: " + tmpPathInfo.getStartFieldInfo().getFieldId() + "\n");

            if(!startField.getFxFieldType().name().equals(tmpPathInfo.getStartFieldInfo().getFieldType())) {
                throw new Exception("Unable to add " + SpeedyFxField.class.getSimpleName() + ". The type in the FXML file doesn't match." +
                        "\n\nMap (" + tmpPathInfo.getStartFieldInfo().getFieldId() +  ")" +
                        " / FXML (" + startField.getFieldId() + "):\n" +
                        tmpPathInfo.getStartFieldInfo().getFieldType() + "/" + startField.getFxFieldType().name() + "\n");
            }

            //Find end field.
            SpeedyFxField endField = findSpeedyFxField(speedyFxFields, tmpPathInfo.getEndFieldInfo().getFieldId());

            if(endField == null)
                throw new Exception("Unable to find " + SpeedyFxField.class.getSimpleName() + " with id: " + tmpPathInfo.getEndFieldInfo().getFieldId() + "\n");

            if(!endField.getFxFieldType().name().equals(tmpPathInfo.getEndFieldInfo().getFieldType())) {
                throw new Exception("Unable to add " + SpeedyFxField.class.getSimpleName() + ". The type in the FXML file doesn't match." +
                        "\n\nMap (" + tmpPathInfo.getEndFieldInfo().getFieldId() +  ")" +
                        " / FXML (" + endField.getFieldId() + "):\n" +
                        tmpPathInfo.getEndFieldInfo().getFieldType() + "/" + endField.getFxFieldType().name() + "\n");
            }

            //Add field mapping.
            if(!fieldsMapping.containsKey(startField))
                fieldsMapping.put(startField, new LinkedList<>());

            if(!fieldsMapping.get(startField).contains(endField))
                fieldsMapping.get(startField).add(endField);

            if(!fieldsMapping.containsKey(endField))
                fieldsMapping.put(endField, new LinkedList<>());

            if(!fieldsMapping.get(endField).contains(startField))
                fieldsMapping.get(endField).add(startField);

            //Add tile mapping.
            Tile startTile = fieldTileMapping.get(startField);
            Tile endTile = fieldTileMapping.get(endField);

            if(!tilesMapping.containsKey(startTile))
                tilesMapping.put(startTile, new LinkedList<>());

            if(!tilesMapping.get(startTile).contains(endTile))
                tilesMapping.get(startTile).add(endTile);

            if(!tilesMapping.containsKey(endTile))
                tilesMapping.put(endTile, new LinkedList<>());

            if(!tilesMapping.get(endTile).contains(startTile))
                tilesMapping.get(endTile).add(startTile);
        }

        //Set adjacent tiles.
        for(var tmpEntry : tilesMapping.entrySet())
            tmpEntry.getKey().setAdjacent(tmpEntry.getValue());

        //Find fox tile if present.
        Tile startTile = fieldTileMapping.get(hedgehogStartField);

        Tile firstTile = startTile;
        Tile foxOffsetTile = null;

        for(Tile tmpChildTile : startTile.getAdjacent()) {
            if(tmpChildTile.getTileType() == TileType.FOX_OFFSET) {
                foxOffsetTile = tmpChildTile;
                break;
            }
        }

        if(foxOffsetTile != null) {
            Tile activeFoxOffsetTile = foxOffsetTile;
            Tile lastFoxOffsetTile = foxOffsetTile;
            boolean searchPreviousFoxTile = true;

            while (searchPreviousFoxTile) {
                boolean previousFoxTileExists = false;

                for(Tile tmpChildTile : activeFoxOffsetTile.getAdjacent()) {
                    if(tmpChildTile != lastFoxOffsetTile && tmpChildTile.getTileType() == TileType.FOX_OFFSET) {
                        lastFoxOffsetTile = activeFoxOffsetTile;
                        activeFoxOffsetTile = tmpChildTile;
                        previousFoxTileExists = true;

                        break;
                    }
                }

                if(!previousFoxTileExists)
                    searchPreviousFoxTile = false;
            }

            firstTile = activeFoxOffsetTile;
        }

        //Set map data.
        MapData mapData = new MapData();

        mapData.setRootNode(rootNode);

        mapData.setMapInfo(mapInfo);

        mapData.setFirstTile(firstTile);
        mapData.setStartTile(startTile);

        mapData.setFieldsMapping(fieldsMapping);
        mapData.setTilesMapping(tilesMapping);

        mapData.setSpeedyFxFields(speedyFxFields);
        mapData.setTiles(tiles);

        mapData.setFieldTileMapping(fieldTileMapping);
        mapData.setTileFieldMapping(tileFieldMapping);

        return mapData;
    }

    private static TileType fieldTypeToTileType(SpeedyFxFieldTypes fieldType) throws Exception {
        if(fieldType == SpeedyFxFieldTypes.FOX_OFFSET) {
            return TileType.FOX_OFFSET;
        }
        else if(fieldType == SpeedyFxFieldTypes.START_FIELD) {
            return TileType.HEDGEHOG_START;
        }
        else if(fieldType == SpeedyFxFieldTypes.END_FIELD) {
            return TileType.END;
        }
        else if(fieldType == SpeedyFxFieldTypes.APPLE_ITEM_FIELD) {
            return TileType.APPLE;
        }
        else if(fieldType == SpeedyFxFieldTypes.MUSHROOM_ITEM_FIELD) {
            return TileType.MUSHROOM;
        }
        else if(fieldType == SpeedyFxFieldTypes.LEAF_ITEM_FIELD) {
            return TileType.LEAF;
        }

        throw new Exception("Missing mapping from " + SpeedyFxFieldTypes.class.getSimpleName() + " to " + TileType.class.getSimpleName());
    }

    private static SpeedyFxFieldTypes createRandomFieldType() {
        while(true) {
            int randomFieldTypeId = new Random().nextInt(SpeedyFxFieldTypes.values().length);
            SpeedyFxFieldTypes randomFieldType = SpeedyFxFieldTypes.values()[randomFieldTypeId];

            if(randomFieldType == SpeedyFxFieldTypes.APPLE_ITEM_FIELD || randomFieldType == SpeedyFxFieldTypes.LEAF_ITEM_FIELD ||
                randomFieldType == SpeedyFxFieldTypes.MUSHROOM_ITEM_FIELD) {
                return randomFieldType;
            }
        }
    }

    private static List<SpeedyFxField> readAvailableSpeedyFxFields(Pane rootNode) {
        List<SpeedyFxField> speedyFxFields = new LinkedList<>();

        for(Node tmpNode : rootNode.getChildrenUnmodifiable()) {
            if(tmpNode instanceof SpeedyFxField)
                speedyFxFields.add((SpeedyFxField) tmpNode);
        }

        return speedyFxFields;
    }

    private static SpeedyFxField findSpeedyFxField(List<SpeedyFxField> speedyFxFields, String fieldId) {
        SpeedyFxField foundField = null;

        for (SpeedyFxField tmpSpeedyFxField : speedyFxFields) {
            if (tmpSpeedyFxField.getFieldId().equals(fieldId)) {
                foundField = tmpSpeedyFxField;
                break;
            }
        }

        return foundField;
    }
}
