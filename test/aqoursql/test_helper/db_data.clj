(ns aqoursql.test-helper.db-data)

(def artist
  [{:id 1
    :type 1
    :name "Aqours"}
   {:id 2
    :type 1
    :name "CYaRon!"}
   {:id 3
    :type 1
    :name "AZALEA"}
   {:id 4
    :type 1
    :name "Guilty Kiss"}
   {:id 5
    :type 1
    :name "Saint Snow"}
   {:id 6
    :type 1
    :name "Saint Aqours Snow"}])

(def artist_member
  [{:artist_id 1
    :member_id 1}
   {:artist_id 1
    :member_id 2}
   {:artist_id 1
    :member_id 3}
   {:artist_id 2
    :member_id 2}
   {:artist_id 3
    :member_id 1}
   {:artist_id 4
    :member_id 3}
   {:artist_id 5
    :member_id 4}
   {:artist_id 6
    :member_id 1}
   {:artist_id 6
    :member_id 2}
   {:artist_id 6
    :member_id 3}
   {:artist_id 6
    :member_id 4}])

(def member
  [{:id 1
    :name "黒澤 ダイヤ"
    :organization_id 1}
   {:id 2
    :name "渡辺 曜"
    :organization_id 1}
   {:id 3
    :name "津島 善子"
    :organization_id 1}
   {:id 4
    :name "鹿角 理亞"
    :organization_id 2}])

(def organization
  [{:id 1
    :name "浦の星女学院"}
   {:id 2
    :name "函館聖泉女子高等学院"}])

(def song
  [{:id 1
    :name "君のこころは輝いてるかい"
    :artist_id 1
    :release_date #inst "2015-10-07"}
   {:id 2
    :name "元気全開DAY！DAY！DAY"
    :artist_id 2
    :release_date #inst "2016-05-11"}
   {:id 3
    :name "トリコリコPLEASE!!"
    :artist_id 3
    :release_date #inst "2016-05-25"}
   {:id 4
    :name "Strawberry Trapper"
    :artist_id 4
    :release_date #inst "2016-06-08"}
   {:id 5
    :name "SELF CONTROL!!"
    :artist_id 5
    :release_date #inst "2016-11-30"}
   {:id 6
    :name "Awaken the power"
    :artist_id 6
    :release_date #inst "2017-12-20"}])
