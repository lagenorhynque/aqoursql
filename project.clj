(defproject aqoursql "0.1.0"
  :description "AqoursQL, an example GraphQL API"
  :url "https://github.com/lagenorhynque/aqoursql"
  :min-lein-version "2.8.1"
  :dependencies [[camel-snake-kebab "0.4.2"]
                 [clojure.java-time "0.3.2"]
                 [com.walmartlabs/lacinia-pedestal "0.14.0"]
                 [duct.module.cambium "1.2.0"]
                 [duct.module.pedestal "2.1.2"]
                 [duct/core "0.8.0" :exclusions [integrant
                                                 medley]]
                 [duct/module.sql "0.6.1"]
                 [funcool/struct "1.4.0" :exclusions [org.clojure/clojurescript]]
                 [honeysql "1.0.444"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [io.pedestal/pedestal.service "0.5.8" :exclusions [cheshire]]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.mariadb.jdbc/mariadb-java-client "2.7.1"]]
  :plugins [[duct/lein-duct "0.12.1"]]
  :middleware [lein-duct.plugin/middleware]
  :main ^:skip-aot aqoursql.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :profiles
  {:repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :dev  [:shared :project/dev :profiles/dev]
   :test [:shared :project/dev :project/test :profiles/test]
   :uberjar [:shared :project/uberjar]

   :shared {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[clj-http "3.11.0"]
                                   [com.bhauman/rebel-readline "0.1.4"]
                                   [com.gearswithingears/shrubbery "0.4.1"]
                                   [eftest "0.5.9" :exclusions [org.clojure/tools.namespace]]
                                   [fipp "0.6.23"]
                                   [hawk "0.2.11"]
                                   [integrant/repl "0.3.2"]
                                   [orchestra "2020.09.18-1"]
                                   [pjstadig/humane-test-output "0.10.0"]
                                   [vincit/venia "0.2.5"]]
                  :plugins [[jonase/eastwood "0.3.12"]
                            [lein-ancient "0.6.15"]
                            [lein-cljfmt "0.7.0"]
                            [lein-cloverage "1.2.1"]
                            [lein-codox "0.10.7"]
                            [lein-kibit "0.1.8"]]
                  :aliases {"rebel" ^{:doc "Run REPL with rebel-readline."}
                            ["trampoline" "run" "-m" "rebel-readline.main"]
                            "test-coverage" ^{:doc "Execute cloverage."}
                            ["cloverage" "--ns-exclude-regex" "^(:?dev|user)$" "--codecov" "--junit"]
                            "lint" ^{:doc "Execute cljfmt check, eastwood and kibit."}
                            ["do"
                             ["cljfmt" "check"]
                             ["eastwood" "{:config-files [\"dev/resources/eastwood_config.clj\"]
                                           :source-paths [\"src\"]
                                           :test-paths []}"]
                             ["kibit"]]}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]
                  :cljfmt {:indents {fdef [[:inner 0]]
                                     for-all [[:inner 0]]
                                     when-valid [[:inner 0]]}}
                  :codox {:output-path "target/codox"
                          :source-uri "https://github.com/lagenorhynque/aqoursql/blob/master/{filepath}#L{line}"
                          :metadata {:doc/format :markdown}}}
   :project/test {}
   :project/uberjar {:aot :all
                     :uberjar-name "aqoursql.jar"}
   :profiles/dev {}
   :profiles/test {}})
