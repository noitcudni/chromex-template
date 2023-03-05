(ns bulk-slack-user-deactivation.popup.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<! chan]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.ext.tabs :as tabs]
            [chromex.ext.runtime :as runtime]
            [chromex.chrome-event-channel :refer [make-chrome-event-channel]]
            [chromex.protocols.chrome-port :refer [post-message!]]
            [chromex.ext.runtime :as runtime :refer-macros [connect]]))

; -- a message loop ---------------------------------------------------------------------------------------------------------
(defn handle-on-message [message]
  (prn message)
  )

(defn process-chrome-event [event]
  (let [[event-id event-args] event
        _ (prn ">> popup event: " event) ;;xxx
        _ (prn ">> popup event-id: " event-id) ;;xxx
        _ (prn ">> popup event-args: ", event-args) ;;xxx
        ]
    (case event-id
      :chromex.ext.runtime/on-message (apply handle-on-message event-args)
      nil)
    ))

(defn run-chrome-event-loop! [chrome-event-channel]
  (prn "popup: starting main event loop in popup...")
  (go-loop []
    (when-some [event (<! chrome-event-channel)]
      (prn "event: " event) ;;xxx
      (process-chrome-event event)
      (recur))
    (prn "popup: leaving main event loop")))

(defn boot-chrome-event-loop! []
  (let [chrome-event-channel (make-chrome-event-channel (chan))]
    ;; (tabs/tap-all-events chrome-event-channel)
    ;; (runtime/tap-all-events chrome-event-channel)
    (runtime/tap-on-message-events chrome-event-channel)
    (run-chrome-event-loop! chrome-event-channel)))


; -- main entry point -------------------------------------------------------------------------------------------------------

(defn init! []
  (log "POPUP: init")
  (boot-chrome-event-loop!)
  ;; send the first message
  (runtime/send-message nil "popup: hello"))
