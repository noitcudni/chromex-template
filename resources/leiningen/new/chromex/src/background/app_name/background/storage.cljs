(ns {{name}}.background.storage
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<! chan]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.protocols.chrome-storage-area :refer [get set]]
            [chromex.ext.storage :as storage]))

(defn store-local-storage [k]
  (fn [v]
    (let [local-storage (storage/get-local)]
     (go
       (storage-area/set local-storage (clj->js {k v})))
     )))

(defn get-local-storage [k]
  (fn []
    (let [local-storage (storage/get-local)]
      (go
        (let [[[items] error] (<! (storage-area/get local-storage k))]
          (-> items js->clj (get k))
          )))))

(defn remove-local-storage [k]
  (fn []
    (let [local-storage (storage/get-local)]
      (go
        (<! (storage-area/remove local-storage k)))
      )))

(defn clear-storage []
  (let [local-storage (storage/get-local)]
    (go
      (<! (storage-area/clear local-storage)))))
