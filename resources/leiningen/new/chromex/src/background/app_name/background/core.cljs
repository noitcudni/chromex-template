(ns {{name}}.background.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [goog.string :as gstring]
            [goog.string.format]
            [cljs.core.async :refer [<! chan]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.chrome-event-channel :refer [make-chrome-event-channel]]
            [chromex.protocols.chrome-port :refer [post-message! get-sender]]
            [chromex.ext.tabs :as tabs]
            [chromex.ext.runtime :as runtime]
            [{{name}}.content-script.common :as common]
            [{{name}}.background.storage :refer [test-storage!]]))

(def curr-tab-id-atom (atom nil))


; -- event handlers ---------------------------------------------------------------------------------------------------------
(defn handle-on-message [message]
  (go
    (let [{:keys [type] :as whole-edn} (common/unmarshall message)
          _ (prn ">> handle-on-message" whole-edn)
          ;; NOTE: to send a message to content script
          ;; call (tabs/send-message @curr-tab-id-atom (common/marshall {:type :done-init-victims}))
          ]
      (cond
        (= type :hello-world) (do
                                (prn ">> got hello-world" whole-edn))
        (= type :reset-tab-id) (do
                                 (prn ">> storing new tab-id: " (:tab-id whole-edn))
                                 (reset! curr-tab-id-atom (:tab-id whole-edn))))
      )))


; -- main event loop --------------------------------------------------------------------------------------------------------

(defn process-chrome-event [event-num event]
  (log (gstring/format "BACKGROUND: got chrome event (%05d)" event-num) event)
  (let [[event-id event-args] event]
    (case event-id
      ::runtime/on-message (apply handle-on-message event-args)
      nil)))

(defn run-chrome-event-loop! [chrome-event-channel]
  (log "BACKGROUND: starting main event loop...")
  (go-loop [event-num 1]
    (when-some [event (<! chrome-event-channel)]
      (process-chrome-event event-num event)
      (recur (inc event-num)))
    (log "BACKGROUND: leaving main event loop")))

(defn boot-chrome-event-loop! []
  (let [chrome-event-channel (make-chrome-event-channel (chan))]
    (runtime/tap-on-message-events chrome-event-channel)
    (run-chrome-event-loop! chrome-event-channel)))

; -- main entry point -------------------------------------------------------------------------------------------------------

(defn init! []
  (log "BACKGROUND: init")
  (boot-chrome-event-loop!))
