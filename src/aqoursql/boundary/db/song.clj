(ns aqoursql.boundary.db.song
  (:require [aqoursql.boundary.db.artist :as artist]
            [aqoursql.boundary.db.core :as db]
            [clojure.spec.alpha :as s]
            [duct.database.sql]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [merge-order-by merge-where]]))

(s/def ::id nat-int?)
(s/def ::name string?)
(s/def ::artist_id ::artist/id)
(s/def ::release_date #(instance? java.util.Date %))

(s/def ::song
  (s/keys :req-un [::id
                   ::name
                   ::artist_id
                   ::release_date]))

(s/fdef find-song-by-id
  :args (s/cat :db ::db/db
               :id ::id)
  :ret (s/nilable ::song))

(s/fdef find-songs
  :args (s/cat :db ::db/db
               :tx-data (s/nilable (s/keys :opt-un [::name]))))

(defprotocol Song
  (find-song-by-id [db id])
  (find-songs [db tx-data]))

(def sql-song
  (sql/build
   :select :s.*
   :from [[:song :s]]))

(extend-protocol Song
  duct.database.sql.Boundary
  (find-song-by-id [db id]
    (db/select-first db (merge-where sql-song [:= :s.id id])))
  (find-songs [db {:keys [name]}]
    (db/select db (cond-> sql-song
                    name (merge-where [:like :s.name (str \% name \%)])
                    true (merge-order-by [:s.id :asc])))))
