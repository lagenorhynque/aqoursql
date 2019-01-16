(ns aqoursql.main
  (:gen-class)
  (:require [duct.core :as duct]))

(duct/load-hierarchy)

(defn -main [& args]
  (let [keys     (or (duct/parse-keys args) [:duct/daemon])
        profiles [:duct.profile/prod]]
    (-> (duct/resource "aqoursql/config.edn")
        (duct/read-config)
        (duct/exec-config profiles keys))))
