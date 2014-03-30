import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  override def compileOptions:Seq[CompileOption] = List( /*Verbose,*/ Unchecked, Deprecation)

  //override def packageOptions = Seq(MainClass("main.scala.squirt.Main"))

  //val scalatools = "scala-tools" at "http://scala-tools.org/repo-snapshots"

  //val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test->default"
  
  //val scalacheck = "org.scala-tools.testing" % "scalacheck" % "1.6" % "test"

  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")

  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
  
  override def libraryDependencies = Set(
    //"commons-collections" % "commons-collections" % "3.2.1",
    //"io.argonaut" %% "argonaut" % "6.0.2",
    "com.google.guava" % "guava-jdk5" % "16.0",
    "junit" % "junit" % "4.11" % "test",
    "com.novocode" % "junit-interface" % "0.10" % "test",
    "org.specs2" % "specs2_2.9.3" % "1.12.4.1" % "test",
    "com.google.code.findbugs" % "jsr305" % "2.0.3" % "test" // weird workaround for Missing dependency 'class javax.annotation.Nullable'; see http://code.google.com/p/guava-libraries/issues/detail?id=1095
  ) ++ super.libraryDependencies
}
