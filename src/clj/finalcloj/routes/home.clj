(ns finalcloj.routes.home
  (:require
   [finalcloj.layout :as layout]
   [finalcloj.db.core :as db]
   [clojure.java.io :as io]
   [finalcloj.middleware :as middleware]
   [ring.util.response]
   [me.raynes.fs :as fs]
   [ring.middleware.cookies :as ringcookies]
   [ring.util.http-response :as response]))

(defn level-to-filter [level]
  (println "in ltf:" level)
  (cond
    (= level "basic") "[0-4][0-9][0-9]"
    (= level "advanced") "[5-9][0-9][0-9]"
    :else "[0-9][0-9][0-9]"
    )
  )



(defn rand-page [request]
  (def level (get-in request [:cookies "level" :value]))
  (println "got level: " level)
  (def htmlfilter (level-to-filter level))
  (println "htmlfilter:" htmlfilter)
  (def thepage   (fs/base-name (str (rand-nth (fs/glob (str "resources/html/" htmlfilter ".html"))))))


  (println "the PAGE" thepage)
  (layout/render request thepage)
                
)

(defn wrong [request]
  (let [explanation (get-in request [:flash :explanation])]
  (layout/render request "wrong.html" {:explanation explanation}))
  )


(defn right [request]
  (let [explanation (get-in request [:flash :explanation])]
  (layout/render request "right.html" {:explanation explanation}))
  )

(defn setup [{:keys [params]}]
  (println "setup:" params)
  (let [{:keys [level]} params
        level-cookie {:max-age 3600 :value level}]
  (-> (response/found "/question")
      (assoc-in ,,, [:cookies "level"] level-cookie)))
)

(defn answer-proc  [{:keys [params]}]
  (println params) ; Print params in repl, helpful for debugging

  (def rightans (:ra params))
  (def amp (:amp params))
  (def suit (:suit params))
  
  (println rightans)

  (if
      (= (str amp "," suit) rightans)
    (-> (response/found "/right")
        (assoc ,,, :flash params))
    (-> (response/found "/wrong")
        (assoc ,,, :flash params))
    )
  )




(defn wrap-prn [handler]
  (fn [request]
    (prn request)
    (handler request)))


(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [ "" 
   {:middleware [middleware/wrap-csrf
                   middleware/wrap-formats]}
   ["/question" {:get rand-page} ]
   ["/setup" {:post setup} ]
   ["/answer" {:post answer-proc}]
   ["/right" {:get right}]
   ["/wrong" {:get wrong}]
   ["/answer_proc" {:post answer-proc}]
   ["/" {:get home-page}]
   ["/about" {:get about-page}]])

