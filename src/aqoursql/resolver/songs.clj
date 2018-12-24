(ns aqoursql.resolver.songs
  (:require [aqoursql.boundary.db.song :as db.song]
            [clojure.set :as set]
            [com.walmartlabs.lacinia.executor :as executor]))

(defn- song-with-artist [song]
  (let [artist (-> song
                   (select-keys [:artist_id :artist_type :artist_name])
                   (set/rename-keys {:artist_id :id
                                     :artist_type :type
                                     :artist_name :name}))]
    (assoc song :artist artist)))

(defn fetch-song-by-id [{:keys [db] :as context} args _]
  (if (executor/selects-field? context :Song/artist)
    (song-with-artist (db.song/find-song-by-id db (assoc args :with-artist? true)))
    (db.song/find-song-by-id db args)))

(defn list-songs [{:keys [db] :as context} args _]
  (if (executor/selects-field? context :Song/artist)
    (map song-with-artist
         (db.song/find-songs db (assoc args :with-artist? true)))
    (db.song/find-songs db args)))
