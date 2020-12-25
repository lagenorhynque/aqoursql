(ns aqoursql.resolver.songs
  (:require
   [aqoursql.boundary.db.member :as db.member]
   [aqoursql.boundary.db.song :as db.song]
   [aqoursql.util.validator :refer [when-valid]]
   [clojure.set :as set]
   [com.walmartlabs.lacinia.executor :as executor]
   [struct.core :as st]))

(defn- song-with-artist [song]
  (let [artist (-> song
                   (select-keys [:artist_id :artist_type :artist_name])
                   (set/rename-keys {:artist_id :id
                                     :artist_type :type
                                     :artist_name :name}))]
    (assoc song :artist artist)))

(defn fetch-song-by-id [{:keys [db] :as context} args _]
  (cond
    (executor/selects-field? context :Artist/members)
    (when-let [song (db.song/find-song-by-id db (assoc args :with-artist? true))]
      (let [members (db.member/find-members db {:artist_id (:artist_id song)})]
        (-> song
            song-with-artist
            (assoc-in [:artist :members] members))))

    (executor/selects-field? context :Song/artist)
    (some-> (db.song/find-song-by-id db (assoc args :with-artist? true)) song-with-artist)

    :else
    (db.song/find-song-by-id db args)))

(defn list-songs [{:keys [db] :as context} args _]
  (when-valid [[] args [[:name [st/min-count 1]]]]
    (cond
      (executor/selects-field? context :Artist/members)
      (if-let [songs (seq (db.song/find-songs db (assoc args :with-artist? true)))]
        (let [members-map (group-by :artist_id
                                    (db.member/find-members db {:artist_ids (distinct (map :artist_id songs))}))]
          (map (fn [{:keys [artist_id] :as song}]
                 (-> song
                     song-with-artist
                     (assoc-in [:artist :members] (get members-map artist_id))))
               songs))
        [])

      (executor/selects-field? context :Song/artist)
      (map song-with-artist
           (db.song/find-songs db (assoc args :with-artist? true)))

      :else
      (db.song/find-songs db args))))
