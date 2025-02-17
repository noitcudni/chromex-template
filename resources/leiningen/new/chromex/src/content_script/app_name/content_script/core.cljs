(ns {{name}}.content-script.core
  (:require-macros [cljs.core.async.macros :refer [go-loop go]])
  (:require [cljs.core.async :refer [<!]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.protocols.chrome-port :refer [post-message!]]
            [chromex.ext.runtime :as runtime :refer-macros [connect]]
            [{{name}}.content-script.common :as common]
            ))

(defn handle-on-message [message send-response]
  (let [{:keys [type] :as msg} (common/unmarshall message)]
    (do
      ;; TODO: handling messages based on type goes here
      (log "CONTENT SCRIPT: received message" msg)
      (send-response #js{"response" "done"})
      )))

; -- a message loop ---------------------------------------------------------------------------------------------------------
(defn my-event-listener-factory []
  (fn [& args]
    ;; NOTE: for some reason I need to call sendresponse and return true
    ;; in order to not get "channel has been closed" error"
    (handle-on-message (first args) (last args))
    true
    ))

(defn boot-chrome-event-loop! []
  (let [chrome-event-channel (make-chrome-event-channel (chan))]
    (with-custom-event-listener-factory
      my-event-listener-factory
      (runtime/tap-on-message-events chrome-event-channel))
    ))


; -- main entry point -------------------------------------------------------------------------------------------------------

(defn init! []
  (log "CONTENT SCRIPT: init")
  (boot-chrome-event-loop!)
  (go
    (runtime/send-message nil (common/marshall {:type :hello-world :data [:some :data]}))
    ))
