(ns aqoursql.boundary.db.artist
  (:require
   [aqoursql.boundary.db.core :as db]
   [aqoursql.util.const :as const]
   [clojure.spec.alpha :as s]
   [duct.database.sql]
   [honeysql.core :as sql]
   [honeysql.helpers :refer [merge-order-by merge-where]]))

(s/def ::id nat-int?)
(s/def ::type const/artist-types)
(s/def ::name string?)

(s/def ::artist
  (s/keys :req-un [::id
                   ::type
                   ::name]))

(s/fdef find-artist-by-id
  :args (s/cat :db ::db/db
               :id ::id)
  :ret (s/nilable ::artist))

(s/fdef find-artists
  :args (s/cat :db ::db/db
               :tx-data (s/nilable (s/keys :opt-un [::type
                                                    ::name])))
  :ret (s/coll-of ::artist))

(defprotocol Artist
  (find-artist-by-id [db id])
  (find-artists [db tx-data]))

(def sql-artist
  (sql/build
   :select :a.*
   :from [[:artist :a]]))

(extend-protocol Artist
  duct.database.sql.Boundary
  (find-artist-by-id [db id]
    (db/select-first db (merge-where sql-artist [:= :a.id id])))
  (find-artists [db {:keys [type name]}]
    (db/select db (cond-> sql-artist
                    type (merge-where [:= :a.type type])
                    name (merge-where [:like :a.name (str \% name \%)])
                    true (merge-order-by [:a.id :asc])))))
