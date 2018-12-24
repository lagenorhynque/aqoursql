(ns aqoursql.resolver.artists
  (:require [aqoursql.boundary.db.artist :as db.artist]))

(defn fetch-artist-by-id [{:keys [db]} {:keys [id]} _]
  (db.artist/find-artist-by-id db id))

(defn list-artists [{:keys [db]} args _]
  (db.artist/find-artists db args))
