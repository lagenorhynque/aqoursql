(ns aqoursql.resolver.artists-test
  (:require [aqoursql.resolver.artists :as sut]
            [aqoursql.test-helper.core :as helper :refer [with-db-data with-system]]
            [aqoursql.test-helper.db-data :as db-data]
            [clojure.test :as t]
            [venia.core :as venia]))

(t/use-fixtures
  :once
  helper/instrument-specs)

(t/deftest test-fetch-artist-by-id
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :artist_member db-data/artist_member
                        :member db-data/member
                        :organization db-data/organization}]
      (t/testing "membersを指定した場合"
        (t/testing "取得できる"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   #:venia{:queries [[:artist_by_id {:id 1}
                                                      [:id
                                                       :type
                                                       :name
                                                       [:members
                                                        [:id
                                                         :name
                                                         :organization_id
                                                         :organization_name]]]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:artist_by_id {:id 1
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
                                                       :organization_name "浦の星女学院"}]}}}
                     (-> body helper/<-json)))))
        (t/testing "存在しないIDを指定すると取得できない"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   #:venia{:queries [[:artist_by_id {:id 100}
                                                      [:id
                                                       :type
                                                       :name
                                                       [:members
                                                        [:id
                                                         :name
                                                         :organization_id
                                                         :organization_name]]]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:artist_by_id nil}}
                     (-> body helper/<-json))))))
      (t/testing "memberを指定しない場合"
        (t/testing "取得できる"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   #:venia{:queries [[:artist_by_id {:id 1}
                                                      [:id
                                                       :type
                                                       :name]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:artist_by_id {:id 1
                                            :type 1
                                            :name "Aqours"}}}
                     (-> body helper/<-json)))))
        (t/testing "存在しないIDを指定すると取得できない"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   #:venia{:queries [[:artist_by_id {:id 100}
                                                      [:id
                                                       :type
                                                       :name]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:artist_by_id nil}}
                     (-> body helper/<-json)))))))))

(t/deftest test-list-artists
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :artist_member db-data/artist_member
                        :member db-data/member
                        :organization db-data/organization}]
      (t/testing "絞り込み条件ありの場合"
        (t/testing "membersを指定した場合"
          (t/testing "取得できる"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     #:venia{:queries [[:artists {:name "Aq"}
                                                        [:id
                                                         :type
                                                         :name
                                                         [:members
                                                          [:id
                                                           :name
                                                           :organization_id
                                                           :organization_name]]]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:artists [{:id 1
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
                                         {:id 6
                                          :type 1
                                          :name "Saint Aqours Snow"
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
                                                     :organization_name "浦の星女学院"}
                                                    {:id 4
                                                     :name "鹿角 理亞"
                                                     :organization_id 2
                                                     :organization_name "函館聖泉女子高等学院"}]}]}}
                       (-> body helper/<-json)))))
          (t/testing "該当なしの条件を指定すると空リスト"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     #:venia{:queries [[:artists {:name "μ"}
                                                        [:id
                                                         :type
                                                         :name
                                                         [:members
                                                          [:id
                                                           :name
                                                           :organization_id
                                                           :organization_name]]]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:artists []}}
                       (-> body helper/<-json))))))
        (t/testing "memberを指定しない場合"
          (t/testing "取得できる"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     #:venia{:queries [[:artists {:name "Aq"}
                                                        [:id
                                                         :type
                                                         :name]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:artists [{:id 1
                                          :type 1
                                          :name "Aqours"}
                                         {:id 6
                                          :type 1
                                          :name "Saint Aqours Snow"}]}}
                       (-> body helper/<-json)))))
          (t/testing "該当なしの条件を指定すると空リスト"
            (let [{:keys [status body]}
                  (helper/http-post sys "/graphql"
                                    (venia/graphql-query
                                     #:venia{:queries [[:artists {:name "μ"}
                                                        [:id
                                                         :type
                                                         :name]]]}))]
              (t/is (= 200 status))
              (t/is (= {:data {:artists []}}
                       (-> body helper/<-json)))))))
      (t/testing "絞り込み条件なしの場合"
        (let [{:keys [status body]}
              (helper/http-post sys "/graphql"
                                (venia/graphql-query
                                 #:venia{:queries [[:artists
                                                    [:name
                                                     [:members
                                                      [:name]]]]]}))]
          (t/is (= 200 status))
          (t/is (= {:data {:artists [{:name "Aqours"
                                      :members [{:name "黒澤 ダイヤ"}
                                                {:name "渡辺 曜"}
                                                {:name "津島 善子"}]}
                                     {:name "CYaRon!"
                                      :members [{:name "渡辺 曜"}]}
                                     {:name "AZALEA"
                                      :members [{:name "黒澤 ダイヤ"}]}
                                     {:name "Guilty Kiss"
                                      :members [{:name "津島 善子"}]}
                                     {:name "Saint Snow"
                                      :members [{:name "鹿角 理亞"}]}
                                     {:name "Saint Aqours Snow"
                                      :members [{:name "黒澤 ダイヤ"}
                                                {:name "渡辺 曜"}
                                                {:name "津島 善子"}
                                                {:name "鹿角 理亞"}]}]}}
                   (-> body helper/<-json))))))))
