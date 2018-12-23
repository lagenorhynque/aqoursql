(ns aqoursql.resolver.members)

(defn fetch-member-by-id [{:keys [db]} {:keys [id]} _]
  (get {1 {:id 1
           :name "高海 千歌"
           :organization_id 1
           :organization_name "浦の星女学院"}}
       id))
