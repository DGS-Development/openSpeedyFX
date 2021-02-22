package de.dgs.apps.openspeedyfx.game.resourcepacks;

import java.io.InputStream;

/*
Copyright 2021 DGS-Development (https://github.com/DGS-Development)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

public class DefaultResourcepack implements Resourcepack {
    private static final String BASE_PATH = "/assets/resourcepack/default";

    private static final class InstanceContainer {
        public static final DefaultResourcepack INSTANCE = new DefaultResourcepack();
    }

    public static DefaultResourcepack getInstance () {
        return InstanceContainer.INSTANCE;
    }

    private DefaultResourcepack() {
        //Block...
    }

    @Override
    public boolean hasResource(String resourcePath) {
        return getClass().getResource(BASE_PATH + resourcePath) != null;
    }

    @Override
    public InputStream getResourceAsStream(String resourcePath) {
        return getClass().getResourceAsStream(BASE_PATH + resourcePath);
    }
}
