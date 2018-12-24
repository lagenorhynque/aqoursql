(ns aqoursql.resolver.artists
  (:require [aqoursql.boundary.db.artist :as db.artist]))

(defn fetch-artist-by-id [{:keys [db]} {:keys [id]} _]
  (db.artist/find-artist-by-id db id))

(defn fetch-artist-by-artist-id [{:keys [db]} _ {:keys [artist_id]}]
  (db.artist/find-artist-by-id db artist_id))

(defn list-artists [{:keys [db]} args _]
  (db.artist/find-artists db args))
