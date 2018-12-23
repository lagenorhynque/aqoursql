(ns aqoursql.resolver.members
  (:require [aqoursql.boundary.db.member :as db.member]))

(defn fetch-member-by-id [{:keys [db]} {:keys [id]} _]
  (db.member/find-member-by-id db id))
