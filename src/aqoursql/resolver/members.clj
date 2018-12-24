(ns aqoursql.resolver.members
  (:require [aqoursql.boundary.db.member :as db.member]))

(defn fetch-member-by-id [{:keys [db]} {:keys [id]} _]
  (db.member/find-member-by-id db id))

(defn list-members [{:keys [db]} args _]
  (db.member/find-members db args))

(defn list-artist-members [{:keys [db]} _ {:keys [id]}]
  (db.member/find-members db {:artist_id id}))
