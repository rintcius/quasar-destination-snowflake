/*
 * Copyright 2014–2019 SlamData Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quasar.destination.snowflake

import argonaut._, Argonaut._

import org.specs2.mutable.Specification

object SnowflakeConfigSpec extends Specification {
  "parser" >> {
    "parses a valid config" >> {
      val testConfig = Json.obj(
        "accountName" := "foo",
        "user" := "bar",
        "password" := "secret password",
        "databaseName" := "db name",
        "schema" := "public",
        "warehouse" := "warehouse name")

      testConfig.as[SnowflakeConfig].result must beRight(
        SnowflakeConfig(
          AccountName("foo"),
          User("bar"),
          Password("secret password"),
          DatabaseName("db name"),
          Schema("public"),
          Warehouse("warehouse name")))
    }

    "schema defaults to public when not specified" >> {
      val testConfig = Json.obj(
        "accountName" := "foo",
        "user" := "bar",
        "password" := "secret password",
        "databaseName" := "db name",
        "warehouse" := "warehouse name")

      testConfig.as[SnowflakeConfig].result must beRight(
        SnowflakeConfig(
          AccountName("foo"),
          User("bar"),
          Password("secret password"),
          DatabaseName("db name"),
          Schema("public"),
          Warehouse("warehouse name")))
    }
  }

  "configToUri" >> {
    "does not include user/password or username in connection string" >> {
      val testConfig =
        SnowflakeConfig(
          AccountName("foo"),
          User("bar"),
          Password("secret"),
          DatabaseName("db name"),
          Schema("public"),
          Warehouse("warehouse name"))

      SnowflakeConfig.configToUri(testConfig).contains("secret") must beFalse
      SnowflakeConfig.configToUri(testConfig).contains("bar") must beFalse
    }
  }
}
