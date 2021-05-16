(ns finalcloj.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[finalcloj started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[finalcloj has shut down successfully]=-"))
   :middleware identity})
