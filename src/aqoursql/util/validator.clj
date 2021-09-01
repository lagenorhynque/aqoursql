(ns aqoursql.util.validator
  (:require
   [aqoursql.util.const :as const]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :refer [transform-keys]]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
   [malli.core :as m]
   [malli.error :as me]))

;;; validation utilities

(defn format-error [[k v]]
  (->> v
       (transform-keys #(csk/->snake_case_symbol % :separator \_))
       (str/join ", ")
       (str (name k) " ")))

(s/fdef when-valid
  :args (s/cat :binding (s/coll-of any?
                                   :kind vector?
                                   :count 3)
               :body (s/* any?)))

(defmacro when-valid [[resolved-value data schema] & body]
  `(if-let [errors# (me/humanize (m/explain ~schema (or ~data {})))]
     (resolve-as ~resolved-value
                 (map (fn [error#]
                        (const/error-map const/error-code-validation
                                         (format-error error#)))
                      errors#))
     (do ~@body)))

;;; common schemas

(def schema-maybe-non-empty-string
  [:or [:string {:min 1
                 :error/message "should not be empty"}]
   nil?])
