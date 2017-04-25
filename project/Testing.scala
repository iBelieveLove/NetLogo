import sbt._
import Def.{ Initialize, spaceDelimited }
import Keys._

object Testing {
  lazy val fast     = taskKey[Unit]("fast tests")
  lazy val medium   = taskKey[Unit]("medium tests")
  lazy val slow     = taskKey[Unit]("slow tests")
  lazy val language = taskKey[Unit]("language tests")
  lazy val extensionTests = taskKey[Unit]("extension tests")
  lazy val crawl    = taskKey[Unit]("extremely slow tests")

  lazy val testReportersClass  = settingKey[String]("class used to run reporter tests")
  lazy val testCommandsClass   = settingKey[String]("class used to run command tests")
  lazy val testExtensionsClass = settingKey[String]("class used to run extensions tests")
  lazy val testModelsClass     = settingKey[String]("class used to run model tests")
  lazy val testChecksumsClass  = settingKey[String]("class used to run checksum tests")

  lazy val tr = inputKey[Unit]("run reporter tests")
  lazy val tc = inputKey[Unit]("run command tests")
  lazy val te = inputKey[Unit]("run extension tests")
  lazy val tm = inputKey[Unit]("run model tests")
  lazy val ts = inputKey[Unit]("run checksum tests")

  lazy val testTempDirectory = settingKey[File]("Temp directory for tests to write files to")

  private val testKeys = Seq(tr, tc, te, tm, ts)

  lazy val suiteSettings = Seq(
    (fast in Test) := {
      (testOnly in Test).toTask(" -- -l org.nlogo.util.SlowTestTag -l org.nlogo.util.ExtensionTestTag -l org.nlogo.headless.LanguageTestTag").value
    },
    (medium in Test) := {
      (testOnly in Test).toTask(" -- -n org.nlogo.headless.LanguageTestTag -l org.nlogo.util.ExtensionTestTag").value
    },
    (language in Test) := {
      (testOnly in Test).toTask(" -- -n org.nlogo.headless.LanguageTestTag").value
    },
    (crawl in Test) := {
      (testOnly in Test).toTask(" -- -n org.nlogo.util.SlowTestTag").value
    },
    (extensionTests in Test) := {
      (testOnly in Test).toTask(" -- -n org.nlogo.util.ExtensionTestTag").value
    },
    (slow in Test) := {
      (testOnly in Test).toTask(" -- -n org.nlogo.util.SlowTestTag -l org.nlogo.headless.LanguageTestTag -l org.nlogo.util.ExtensionTestTag").value
    })

  def useLanguageTestPrefix(prefix: String) =
    inConfig(Test)(
      Seq(
        testReportersClass  := prefix + "Reporters",
        testCommandsClass   := prefix + "Commands",
        testExtensionsClass := prefix + "Extensions",
        testModelsClass     := prefix + "Models")
    )

  val settings = suiteSettings ++
    inConfig(Test)(
      Seq(
        testTempDirectory := file("tmp"),
        testOnly := {
          IO.createDirectory(testTempDirectory.value)
          testOnly.evaluated
        },
        test     := {
          IO.createDirectory(testTempDirectory.value)
          test.value
        },
        ts := keyValueTest(testModelsClass, "model").evaluated
      ) ++
      testKeys.flatMap(key =>
          Defaults.defaultTestTasks(key) ++
          Defaults.testTaskOptions(key)) ++
      (Map(
        tr -> testReportersClass,
        tc -> testCommandsClass,
        te -> testExtensionsClass,
        tm -> testModelsClass).flatMap {
          case (ik, classNameSetting) =>
            Seq[Setting[_]](ik := taggedTest(classNameSetting).evaluated)
        }))

  def taggedTest(className: SettingKey[String]): Def.Initialize[InputTask[Unit]] =
    Def.inputTaskDyn {
      val name = className.value
      val args = Def.spaceDelimited("<arg>").parsed
      val scalaTestArgs =
        if (args.isEmpty) ""
        else args.mkString(" -- -z \"", " ", "\"")
      (testOnly in Test).toTask(s" $name$scalaTestArgs")
    }

  def keyValueTest(className: SettingKey[String], key: String): Def.Initialize[InputTask[Unit]] =
    Def.inputTaskDyn {
      val name = className.value
      val args = Def.spaceDelimited("<arg>").parsed
      val scalaTestArgs =
        if (args.isEmpty)
          ""
        else
          s""" -- "-D$key=${args.mkString(" ")}""""
      (testOnly in Test).toTask(s" $name$scalaTestArgs")
    }
}
