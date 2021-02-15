package de.dgs.apps.openspeedyfx.game.resourcepacks;

public class ResourcepackPaths {
    public static class Fields {
        public static final String BASE_PATH = "/fields";
        public static final String APPLE_FIELD_PNG = BASE_PATH + "/appleField.png";
        public static final String LEAF_FIELD_PNG = BASE_PATH + "/leafField.png";
        public static final String MUSHROOM_FIELD_PNG = BASE_PATH + "/mushroomField.png";
        public static final String END_FIELD_PNG = BASE_PATH + "/endField.png";
    }

    public static class Figures {
        public static final String BASE_PATH = "/figures";
        public static final String HEDGEHOG_BALL_PNG = BASE_PATH + "/hedgehogBall.png";
        public static final String HEDGEHOG_CAPTURED_PNG = BASE_PATH + "/hedgehogCaptured.png";
        public static final String HEDGEHOG_PLAYER_BLUE_PNG = BASE_PATH + "/hedgehogPlayerBlue.png";
        public static final String HEDGEHOG_PLAYER_GREEN_PNG = BASE_PATH + "/hedgehogPlayerGreen.png";
        public static final String HEDGEHOG_PLAYER_RED_PNG = BASE_PATH + "/hedgehogPlayerRed.png";
        public static final String HEDGEHOG_PLAYER_YELLOW_PNG = BASE_PATH + "/hedgehogPlayerYellow.png";
    }

    public static class Fonts {
        public static final String BASE_PATH = "/fonts";
        public static final String SILLY_FONT_TTF = BASE_PATH + "/sillyFont1.ttf";
    }

    public static class Music {
        public static final String BASE_PATH = "/music";
        public static final String MENU_LOOP_OGG = BASE_PATH + "/menuLoop.wav";
        public static final String STORY_MAIN_THEME_OGG = BASE_PATH + "/storyMainTheme.ogg";
    }

    public static class Sounds {
        public static final String BASE_PATH = "/sounds";

        public static class Ballscene {
            public static final String BASE_PATH = Sounds.BASE_PATH + "/ballscene";

            public static final String APPLE_1_WAV = BASE_PATH + "/apple1.wav";
            public static final String APPLE_2_WAV = BASE_PATH + "/apple2.wav";

            public static final String LEAFS_1_WAV = BASE_PATH + "/leafs1.wav";
            public static final String LEAFS_2_WAV = BASE_PATH + "/leafs2.wav";

            public static final String MUSHROOM_1_WAV = BASE_PATH + "/mushroom1.wav";
            public static final String MUSHROOM_2_WAV = BASE_PATH + "/mushroom2.wav";

            public static final String WALLCOLLISION_1_WAV = BASE_PATH + "/wallcollision1.wav";
            public static final String WALLCOLLISION_2_WAV = BASE_PATH + "/wallcollision2.wav";
            public static final String WALLCOLLISION_3_WAV = BASE_PATH + "/wallcollision3.wav";
            public static final String WALLCOLLISION_4_WAV = BASE_PATH + "/wallcollision4.wav";

            public static final String SUCCESS_WAV = BASE_PATH + "/success.wav";
            public static final String FAILURE_WAV = BASE_PATH + "/failure.wav";
        }
    }

    public static class Textures {
        public static final String BASE_PATH = "/textures";

        public static class Ballscene {
            public static final String BASE_PATH = Textures.BASE_PATH + "/ballscene";
            public static final String BORDER_TEXTURE_PNG = BASE_PATH + "/borderTexture.png";
            public static final String FLOOR_TEXTURE_PNG = BASE_PATH + "/floorTexture.png";
        }
    }
}
