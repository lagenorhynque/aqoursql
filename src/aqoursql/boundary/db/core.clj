(ns aqoursql.boundary.db.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [duct.database.sql]
   [honey.sql :as sql]
   [next.jdbc]
   [next.jdbc.quoted]
   [next.jdbc.result-set]
   [next.jdbc.sql]))

;;; DB access utilities

(s/def ::spec any?)
(s/def ::db (s/keys :req-un [::spec]))
(s/def ::sql-map (s/map-of keyword? any?))
(s/def ::table keyword?)
(s/def ::row-map (s/map-of keyword? any?))
(s/def ::row-count (s/and integer? (complement neg?)))
(s/def ::row-id (s/and integer? pos?))

(def sql-format-opts
  {:dialect :mysql})

(s/fdef escape-like-param
  :args (s/cat :s string?)
  :ret string?)

(defn escape-like-param [s]
  (str/replace s #"[\\_%]" "\\\\$0"))

(s/fdef select
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret (s/coll-of ::row-map))

(defn select [{{:keys [datasource]} :spec} sql-map]
  (next.jdbc.sql/query datasource (sql/format sql-map sql-format-opts)
                       {:builder-fn next.jdbc.result-set/as-unqualified-maps}))

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

(defn insert! [{{:keys [datasource]} :spec} table row-map]
  (-> datasource
      (next.jdbc.sql/insert! table
                             row-map
                             {:table-fn (next.jdbc.quoted/schema next.jdbc.quoted/mysql)
                              :column-fn next.jdbc.quoted/mysql
                              :builder-fn next.jdbc.result-set/as-unqualified-maps})
      :insert_id))

(s/fdef insert-multi!
  :args (s/cat :db ::db
               :table ::table
               :row-maps (s/coll-of ::row-map :min-count 1))
  :ret ::row-count)

(defn insert-multi! [{{:keys [datasource]} :spec} table row-maps]
  (-> datasource
      (next.jdbc/execute-one!  (sql/format {:insert-into table
                                            :values row-maps}
                                           sql-format-opts))
      :next.jdbc/update-count))
