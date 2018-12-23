(ns aqoursql.boundary.db.core
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [duct.database.sql]
            [honeysql.core :as sql]))

;;; DB access utilities

(s/def ::spec any?)
(s/def ::db (s/keys :req-un [::spec]))
(s/def ::sql-map (s/map-of keyword? any?))
(s/def ::table keyword?)
(s/def ::row-map (s/map-of keyword? any?))
(s/def ::row-count (s/and integer? (complement neg?)))
(s/def ::row-id (s/and integer? pos?))

(def quoting
  :mysql)
(def identifier-quote
  \`)

(s/fdef select
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret (s/coll-of ::row-map))

(defn select [{:keys [spec]} sql-map]
  (jdbc/query spec (sql/format sql-map :quoting quoting)))

(s/fdef select-first
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret (s/nilable ::row-map))

(defn select-first [db sql-map]
  (first (select db sql-map)))

(s/fdef select-count
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret ::row-count)

(defn select-count [db sql-map]
  (:row-count (select-first db (assoc sql-map
                                      :select [[:%count.* :row-count]]))))

(s/fdef insert!
  :args (s/cat :db ::db
               :table ::table
               :row-map ::row-map)
  :ret ::row-id)

(defn insert! [{:keys [spec]} table row-map]
  (-> (jdbc/insert! spec table row-map {:entities (jdbc/quoted identifier-quote)})
      first
      :insert_id))

(s/fdef insert-multi!
  :args (s/cat :db ::db
               :table ::table
               :row-maps (s/coll-of ::row-map :min-count 1))
  :ret ::row-count)

(defn insert-multi! [{:keys [spec]} table row-maps]
  (first (jdbc/execute! spec (sql/format (sql/build :insert-into table
                                                    :values row-maps)
                                         :quoting quoting))))
