(ns finalcloj.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [finalcloj.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[finalcloj started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[finalcloj has shut down successfully]=-"))
   :middleware wrap-dev})
