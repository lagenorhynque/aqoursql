(ns aqoursql.util.validator
  (:require
   [aqoursql.util.const :as const]
   [clojure.spec.alpha :as s]
   [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
   [struct.core :as st]))

;;; validation utilities

(s/fdef when-valid
  :args (s/cat :binding (s/coll-of any?
                                   :kind vector
                                   :count 3)
               :body (s/* any?)))

(defmacro when-valid [[resolved-value data schema] & body]
  `(if-let [errors# (first (st/validate ~data ~schema))]
     (resolve-as ~resolved-value
                 (map (fn [[k# v#]]
                        {:message (str (name k#) " " v#)
                         :extensions {:code const/error-code-validation}})
                      errors#))
     (do ~@body)))
