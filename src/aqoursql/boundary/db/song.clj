(ns aqoursql.boundary.db.song
  (:require
   [aqoursql.boundary.db.artist :as artist]
   [aqoursql.boundary.db.core :as db]
   [clojure.spec.alpha :as s]
   [duct.database.sql]
   [honey.sql.helpers :refer [join order-by select where]]))

(s/def ::id nat-int?)
(s/def ::name string?)
(s/def ::artist_id ::artist/id)
(s/def ::release_date #(instance? java.util.Date %))

(s/def ::with-artist? boolean?)
(s/def ::artist_type ::artist/type)
(s/def ::artist_name ::artist/name)

(s/def ::song
  (s/keys :req-un [::id
                   ::name
                   ::artist_id
                   ::release_date]
          :opt-un [::artist_type
                   ::artist_name]))

(s/fdef find-song-by-id
  :args (s/cat :db ::db/db
               :tx-data (s/keys :req-un [::id]
                                :opt-un [::with-artist?]))
  :ret (s/nilable ::song))

(s/fdef find-songs
  :args (s/cat :db ::db/db
               :tx-data (s/nilable (s/keys :opt-un [::name
                                                    ::with-artist?]))))

(defprotocol Song
  (find-song-by-id [db tx-data])
  (find-songs [db tx-data]))

(def sql-song
  {:select :s.*
   :from [[:song :s]]})

(defn select-artist [sql]
  (-> sql
      (select [:a.type :artist_type]
              [:a.name :artist_name])
      (join [:artist :a]
            [:= :s.artist_id :a.id])))

(extend-protocol Song
  duct.database.sql.Boundary
  (find-song-by-id [db {:keys [id with-artist?]}]
    (db/select-first db (cond-> sql-song
                          with-artist? select-artist
                          id (where [:= :s.id id]))))
  (find-songs [db {:keys [name with-artist?]}]
    (db/select db (cond-> sql-song
                    with-artist? select-artist
                    name (where [:like
                                 :s.name
                                 (str \% (db/escape-like-param name) \%)])
                    true (order-by [:s.id :asc])))))
