(defproject aqoursql "0.1.0"
  :description "AqoursQL, an example GraphQL API"
  :url "https://github.com/lagenorhynque/aqoursql"
  :min-lein-version "2.0.0"
  :dependencies [[com.walmartlabs/lacinia "0.30.0"]
                 [com.walmartlabs/lacinia-pedestal "0.10.0"]
                 [duct.module.pedestal "1.0.0"]
                 [duct/core "0.6.2"]
                 [duct/database.sql.hikaricp "0.3.3" :exclusions [integrant]]
                 [duct/module.logging "0.3.1"]
                 [duct/module.sql "0.4.2"]
                 [fipp "0.6.14"]
                 [honeysql "0.9.4"]
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 [io.pedestal/pedestal.service "0.5.5"]
                 [org.clojure/clojure "1.9.0"]
                 [org.mariadb.jdbc/mariadb-java-client "2.3.0"]]
  :plugins [[duct/lein-duct "0.10.6"]]
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
                  :dependencies   [[clj-http "3.9.1"]
                                   [com.bhauman/rebel-readline "0.1.4"]
                                   [com.gearswithingears/shrubbery "0.4.1"]
                                   [eftest "0.5.4"]
                                   [integrant/repl "0.3.1" :exclusions [integrant]]
                                   [orchestra "2018.12.06-2"]
                                   [pjstadig/humane-test-output "0.9.0"]
                                   [vincit/venia "0.2.5"]]
                  :plugins [[jonase/eastwood "0.3.4"]
                            [lein-ancient "0.6.15"]
                            [lein-cljfmt "0.6.3"]
                            [lein-cloverage "1.0.13"]
                            [lein-codox "0.10.5"]
                            [lein-kibit "0.1.6"]]
                  :aliases {"rebel" ^{:doc "Run REPL with rebel-readline."}
                            ["trampoline" "run" "-m" "rebel-readline.main"]
                            "test-coverage" ^{:doc "Execute cloverage."}
                            ["with-profile" "test"
                             "cloverage" "--ns-exclude-regex" "^(:?dev|user)$" "--codecov" "--junit"]
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
                                     for-all [[:inner 0]]}}
                  :codox {:output-path "target/codox"
                          :source-uri "https://github.com/lagenorhynque/aqoursql/blob/master/{filepath}#L{line}"
                          :metadata {:doc/format :markdown}}}
   :project/test {}
   :project/uberjar {:aot :all
                     :uberjar-name "aqoursql.jar"}
   :profiles/dev {}
   :profiles/test {}})
