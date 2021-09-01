(ns aqoursql.test-helper.core
  (:require
   [aqoursql.test-helper.db :refer [insert-db-data! truncate-all-tables!]]
   [cheshire.core :as cheshire]
   [clj-http.client :as client]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [duct.core :as duct]
   [integrant.core :as ig]
   [orchestra.spec.test :as stest]
   [venia.core :as venia]))

(duct/load-hierarchy)

;;; fixtures

(defn instrument-specs [f]
  (stest/instrument)
  (f))

;;; macros for testing context

(defn test-system []
  (-> (io/resource "aqoursql/config.edn")
      duct/read-config
      (duct/prep-config [:duct.profile/dev :duct.profile/test])))

(s/fdef with-system
  :args (s/cat :binding (s/coll-of any?
                                   :kind vector?
                                   :count 2)
               :body (s/* any?)))

(defmacro with-system [[bound-var binding-expr] & body]
  `(let [~bound-var (ig/init ~binding-expr)]
     (try
       ~@body
       (finally (ig/halt! ~bound-var)))))

(s/fdef with-db-data
  :args (s/cat :binding (s/coll-of any?
                                   :kind vector?
                                   :count 2)
               :body (s/* any?)))

(defmacro with-db-data [[system db-data-map] & body]
  `(let [db# (:duct.database.sql/hikaricp ~system)]
     (try
       (insert-db-data! db# ~db-data-map)
       ~@body
       (finally (truncate-all-tables! db#)))))

;;; HTTP client

(def ^:private url-prefix "http://localhost:")

(defn- server-port [system]
  (get-in system [:duct.server/pedestal :io.pedestal.http/port]))

(defn http-post [system url body & {:as options}]
  (client/post (str url-prefix (server-port system) url)
               (merge {:body body
                       :content-type :json
                       :accept :json
                       :throw-exceptions? false} options)))

;;; GraphQL utilities

(defn- query-json [q]
  (-> q
      (update :query venia/graphql-query)
      cheshire/generate-string))

(s/def ::query (s/map-of keyword? any?))
(s/def ::variables (s/map-of keyword? any?))

(s/def ::q
  (s/keys :req-un [::query]
          :opt-un [::variables]))

(s/fdef run-query
  :args (s/cat :system any?
               :q ::q))

(defn run-query [system q]
  (http-post system "/graphql" (query-json q)))

;;; JSON conversion

(defn ->json [obj]
  (cheshire/generate-string obj))

(defn <-json [str]
  (cheshire/parse-string str true))

;;; misc.

(defn nth-errors [json index]
  (-> json <-json :errors (nth index)))
