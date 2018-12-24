(ns aqoursql.resolver.songs-test
  (:require [aqoursql.resolver.songs :as sut]
            [aqoursql.test-helper.core :as helper :refer [with-db-data with-system]]
            [aqoursql.test-helper.db-data :as db-data]
            [clojure.test :as t]
            [venia.core :as venia]))

(t/use-fixtures
  :once
  helper/instrument-specs)

(t/deftest test-fetch-song-by-id
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :artist_member db-data/artist_member
                        :member db-data/member
                        :organization db-data/organization
                        :song db-data/song}]
      (t/testing "artist, membersを指定した場合"
        (t/testing "取得できる"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:song_by_id {:id 1}
                                                     [:id
                                                      :name
                                                      :artist_id
                                                      [:artist
                                                       [:id
                                                        :type
                                                        :name
                                                        [:members
                                                         [:id
                                                          :name
                                                          :organization_id
                                                          :organization_name]]]]
                                                      :release_date]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:song_by_id {:id 1
                                          :name "君のこころは輝いてるかい"
                                          :artist_id 1
                                          :artist {:id 1
                                                   :type 1
                                                   :name "Aqours"
                                                   :members [{:id 1
                                                              :name "黒澤 ダイヤ"
                                                              :organization_id 1
                                                              :organization_name "浦の星女学院"}
                                                             {:id 2
                                                              :name "渡辺 曜"
                                                              :organization_id 1
                                                              :organization_name "浦の星女学院"}
                                                             {:id 3
                                                              :name "津島 善子"
                                                              :organization_id 1
                                                              :organization_name "浦の星女学院"}]}
                                          :release_date "2015-10-07"}}}
                     (-> body helper/<-json)))))
        (t/testing "存在しないIDを指定すると取得できない"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:song_by_id {:id 100}
                                                     [:id
                                                      :name
                                                      :artist_id
                                                      [:artist
                                                       [:id
                                                        :type
                                                        :name
                                                        [:members
                                                         [:id
                                                          :name
                                                          :organization_id
                                                          :organization_name]]]]
                                                      :release_date]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:song_by_id nil}}
                     (-> body helper/<-json))))))
      (t/testing "artistのみ指定した場合"
        (t/testing "取得できる"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:song_by_id {:id 1}
                                                     [:id
                                                      :name
                                                      :artist_id
                                                      [:artist
                                                       [:id
                                                        :type
                                                        :name]]
                                                      :release_date]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:song_by_id {:id 1
                                          :name "君のこころは輝いてるかい"
                                          :artist_id 1
                                          :artist {:id 1
                                                   :type 1
                                                   :name "Aqours"}
                                          :release_date "2015-10-07"}}}
                     (-> body helper/<-json)))))
        (t/testing "存在しないIDを指定すると取得できない"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:song_by_id {:id 100}
                                                     [:id
                                                      :name
                                                      :artist_id
                                                      [:artist
                                                       [:id
                                                        :type
                                                        :name]]
                                                      :release_date]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:song_by_id nil}}
                     (-> body helper/<-json))))))
      (t/testing "artistもmemberも指定しない場合"
        (t/testing "取得できる"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:song_by_id {:id 1}
                                                     [:id
                                                      :name
                                                      :artist_id
                                                      :release_date]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:song_by_id {:id 1
                                          :name "君のこころは輝いてるかい"
                                          :artist_id 1
                                          :release_date "2015-10-07"}}}
                     (-> body helper/<-json)))))
        (t/testing "存在しないIDを指定すると取得できない"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:song_by_id {:id 100}
                                                     [:id
                                                      :name
                                                      :artist_id
                                                      :release_date]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:song_by_id nil}}
                     (-> body helper/<-json)))))))))

(t/deftest test-list-songs
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :artist_member db-data/artist_member
                        :member db-data/member
                        :organization db-data/organization
                        :song db-data/song}]
      (t/testing "絞り込み条件ありの場合"
        (t/testing "artist, membersを指定した場合"
          (t/testing "取得できる"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     {:venia/queries [[:songs {:name "輝"}
                                                       [:id
                                                        :name
                                                        :artist_id
                                                        [:artist
                                                         [:id
                                                          :type
                                                          :name
                                                          [:members
                                                           [:id
                                                            :name
                                                            :organization_id
                                                            :organization_name]]]]
                                                        :release_date]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:songs [{:id 1
                                        :name "君のこころは輝いてるかい"
                                        :artist_id 1
                                        :artist {:id 1
                                                 :type 1
                                                 :name "Aqours"
                                                 :members [{:id 1
                                                            :name "黒澤 ダイヤ"
                                                            :organization_id 1
                                                            :organization_name "浦の星女学院"}
                                                           {:id 2
                                                            :name "渡辺 曜"
                                                            :organization_id 1
                                                            :organization_name "浦の星女学院"}
                                                           {:id 3
                                                            :name "津島 善子"
                                                            :organization_id 1
                                                            :organization_name "浦の星女学院"}]}
                                        :release_date "2015-10-07"}]}}
                       (-> body helper/<-json)))))
          (t/testing "該当なしの条件を指定すると空リスト"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     {:venia/queries [[:songs {:name "空"}
                                                       [:id
                                                        :name
                                                        :artist_id
                                                        [:artist
                                                         [:id
                                                          :type
                                                          :name
                                                          [:members
                                                           [:id
                                                            :name
                                                            :organization_id
                                                            :organization_name]]]]
                                                        :release_date]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:songs []}}
                       (-> body helper/<-json))))))
        (t/testing "artistのみ指定した場合"
          (t/testing "取得できる"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     {:venia/queries [[:songs {:name "輝"}
                                                       [:id
                                                        :name
                                                        :artist_id
                                                        [:artist
                                                         [:id
                                                          :type
                                                          :name]]
                                                        :release_date]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:songs [{:id 1
                                        :name "君のこころは輝いてるかい"
                                        :artist_id 1
                                        :artist {:id 1
                                                 :type 1
                                                 :name "Aqours"}
                                        :release_date "2015-10-07"}]}}
                       (-> body helper/<-json)))))
          (t/testing "該当なしの条件を指定すると空リスト"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     {:venia/queries [[:songs {:name "空"}
                                                       [:id
                                                        :name
                                                        :artist_id
                                                        [:artist
                                                         [:id
                                                          :type
                                                          :name]]
                                                        :release_date]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:songs []}}
                       (-> body helper/<-json))))))
        (t/testing "artistもmemberも指定しない場合"
          (t/testing "取得できる"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     {:venia/queries [[:songs {:name "輝"}
                                                       [:id
                                                        :name
                                                        :artist_id
                                                        :release_date]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:songs [{:id 1
                                        :name "君のこころは輝いてるかい"
                                        :artist_id 1
                                        :release_date "2015-10-07"}]}}
                       (-> body helper/<-json)))))
          (t/testing "該当なしの条件を指定すると空リスト"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     {:venia/queries [[:songs {:name "空"}
                                                       [:id
                                                        :name
                                                        :artist_id
                                                        :release_date]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:songs []}}
                       (-> body helper/<-json)))))))
      (t/testing "絞り込み条件なしの場合"
        (let [{:keys [status body]}
              (helper/http-post sys "/graphql"
                                (venia/graphql-query
                                 {:venia/queries [[:songs
                                                   [:name
                                                    [:artist
                                                     [:name
                                                      [:members
                                                       [:name]]]]]]]}))]
          (t/is (= 200 status))
          (t/is (= {:data {:songs [{:name "君のこころは輝いてるかい"
                                    :artist {:name "Aqours"
                                             :members [{:name "黒澤 ダイヤ"}
                                                       {:name "渡辺 曜"}
                                                       {:name "津島 善子"}]}}
                                   {:name "元気全開DAY！DAY！DAY!"
                                    :artist {:name "CYaRon!"
                                             :members [{:name "渡辺 曜"}]}}
                                   {:name "トリコリコPLEASE!!"
                                    :artist {:name "AZALEA"
                                             :members [{:name "黒澤 ダイヤ"}]}}
                                   {:name "Strawberry Trapper"
                                    :artist {:name "Guilty Kiss"
                                             :members [{:name "津島 善子"}]}}
                                   {:name "SELF CONTROL!!"
                                    :artist {:name "Saint Snow"
                                             :members [{:name "鹿角 理亞"}]}}
                                   {:name "Awaken the power"
                                    :artist {:name "Saint Aqours Snow"
                                             :members [{:name "黒澤 ダイヤ"}
                                                       {:name "渡辺 曜"}
                                                       {:name "津島 善子"}
                                                       {:name "鹿角 理亞"}]}}]}}
                   (-> body helper/<-json))))))))