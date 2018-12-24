(ns aqoursql.resolver.songs
  (:require [aqoursql.boundary.db.song :as db.song]))

(defn fetch-song-by-id [{:keys [db]} {:keys [id]} _]
  (db.song/find-song-by-id db id))

(defn list-songs [{:keys [db]} args _]
  (db.song/find-songs db args))
