(ns finalcloj.routes.home
  (:require
   [finalcloj.layout :as layout]
   [finalcloj.db.core :as db]
   [clojure.java.io :as io]
   [finalcloj.middleware :as middleware]
   [ring.util.response]
   [me.raynes.fs :as fs]
   [clojure.string :as str]
   [ring.middleware.cookies :as ringcookies]
   [ring.util.http-response :as response]))

(defn level-to-filter [level]
  (println "in ltf:" level)
  (cond
    (= level "basic") "[0-4][0-9][0-9]"
    (= level "advanced") "[5-9][0-9][0-9]"
    (= level "both") "[0-9][0-9][0-9]"
    :else nil
    )
  )



(defn rand-page [request]
  (def level (get-in request [:cookies "level" :value]))
  (println "got level: " level)
  (def htmlfilter (level-to-filter level))
  (println "htmlfilter:" htmlfilter)
  (= htmlfilter nil
     (response/found "/setup")
     )
  (do)
  
  (def thepage   (fs/base-name (str (rand-nth (fs/glob (str "resources/html/" htmlfilter ".html"))))))


  (println "the PAGE" thepage)
   (layout/render request thepage)
   
                
  )


(defn exceptnt [suit]
  (if (= suit "NT")
    suit
    (clojure.string/join "" (drop-last suit))
    ))
    

(defn suitfix [suit amp]
  (if (= (Integer/parseInt amp) 1)
    (exceptnt suit)
     suit
     ))


(defn getans [request]
  (let [ra (get-in request [:flash :ra])]
  
  (let [amp (get-in request [:flash :amp])]
    (println "amp:" amp)
    (let [suit (get-in request [:flash :suit])]
      (def finalsuit (suitfix suit amp))
 { :suit finalsuit :amp amp :ra ra } 
))))

(defn wrong [request]
  (def finalstuff (getans request))
  (def amp (:amp finalstuff))
  (def ra (:ra finalstuff))
  (def finalsuit (:suit finalstuff))

  (let [explanation (get-in request [:flash :explanation])]
 
  (layout/render request "wrong.html" {:ra ra :explanation explanation :ans amp :suit finalsuit}))
  )

(defn rightsuit [ra]
  (str/split ra #",")
  )

(defn right [request]
  (def finalstuff (getans request))
  (def amp (:amp finalstuff))
  (def finalsuit (:suit finalstuff))
  (let [explanation (get-in request [:flash :explanation])]   
  (layout/render request "right.html" {:explanation explanation :ans amp :suit finalsuit})))


(defn setup [{:keys [params]}]
  (println "setup:" params)
  (let [{:keys [level]} params
        level-cookie {:max-age 3600 :value level}]
  (-> (response/found "/question")
      (assoc-in ,,, [:cookies "level"] level-cookie)))
)

(defn answer-proc  [{:keys [params]}]
  (println params) ; Print params in repl, helpful for debugging

  (def rightans (rightsuit (:ra params)))
  (def amp (:amp params))
  (def suit (:suit params))
  
  (println rightans)
  (def ampsuit (str amp suit))
  (println ampsuit)
  (def applyans (apply str rightans))
  (println applyans)

  (if
      (= (str amp suit) (apply str rightans))
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

