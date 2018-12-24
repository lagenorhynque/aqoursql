(ns aqoursql.resolver.artists
  (:require [aqoursql.boundary.db.artist :as db.artist]
            [aqoursql.boundary.db.member :as db.member]
            [com.walmartlabs.lacinia.executor :as executor]))

(defn fetch-artist-by-id [{:keys [db] :as context} {:keys [id]} _]
  (when-let [artist (db.artist/find-artist-by-id db id)]
    (if (executor/selects-field? context :Artist/members)
      (let [members (db.member/find-members db {:artist_id (:id artist)})]
        (assoc artist :members members))
      artist)))

(defn list-artists [{:keys [db] :as context} args _]
  (let [artists (db.artist/find-artists db args)]
    (if (executor/selects-field? context :Artist/members)
      (let [members-map (group-by :artist_id
                                  (db.member/find-members db {:artist_ids (distinct (map :id artists))}))]
        (map (fn [{:keys [id] :as artist}]
               (assoc artist :members (get members-map id)))
             artists))
      artists)))
