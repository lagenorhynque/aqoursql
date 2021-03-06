(ns aqoursql.util.const)

(def ^:const artist-type-group 1)
(def ^:const artist-type-solo 2)
(def artist-types #{artist-type-group artist-type-solo})

(def error-code-validation
  "VALIDATION_ERROR")

(defn error-map [code message]
  {:message message
   :extensions {:code code}})
