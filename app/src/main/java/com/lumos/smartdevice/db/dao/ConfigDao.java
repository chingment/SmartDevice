/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lumos.smartdevice.db.dao;

import android.content.Context;


import com.lumos.smartdevice.R;
import com.lumos.smartdevice.own.AppContext;
import com.lumos.smartdevice.own.AppVar;

import java.util.List;
import java.util.Map;

public class ConfigDao {
	public static final String TABLE_NAME = "tb_config";
	public static final String COLUMN_NAME_FIELD = "field";
	public static final String COLUMN_NAME_VALUE = "value";

	public static final String FIELD_VERSION_MODE = "version_mode";
	public static final String FIELD_SCENE_MODE = "scen_mode";

	public static String getVersionModeName(String val) {

		int id = R.string.unknown;
		switch (val) {
			case AppVar.VERSION_MODE_1:
				id = R.string.vesionmode_1;
				break;
			case AppVar.VERSION_MODE_2:
				id =R.string.vesionmode_2;
				break;
		}
		return	AppContext.getInstance().getResources().getString(id);

	}

	public static String getSceneModeName(String val){

		int id =R.string.unknown;
		switch (val) {
			case AppVar.SCENE_MODE_1:
				id =R.string.scenemode_1;
				break;
			case AppVar.SCENE_MODE_2:
				id =R.string.scenemode_2;
				break;
		}
		return	AppContext.getInstance().getResources().getString(id);

	}
}
