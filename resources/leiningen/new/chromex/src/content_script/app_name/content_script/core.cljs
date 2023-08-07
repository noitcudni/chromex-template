(ns {{name}}.content-script.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<! chan]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.chrome-event-channel :refer [make-chrome-event-channel]]
            [chromex.protocols.chrome-port :refer [post-message!]]
            [chromex.ext.runtime :as runtime :refer-macros [connect]]))

(defn handle-on-message [message]
  (prn ">> message: " message)
  )

; -- a message loop ---------------------------------------------------------------------------------------------------------
(defn process-chrome-event [event-num event]
  ;; (log (gstring/format "BACKGROUND: got chrome event (%05d)" event-num) event)
  (let [[event-id event-args] event
        _ (prn ">> event: " event) ;;xxx
        _ (prn ">> event-id: " event-id) ;;xxx
        _ (prn ">> event-args: ", event-args) ;;xxx
        ]
    (case event-id
      ::runtime/on-connect (apply handle-on-message event-args)
      :chromex.ext.runtime/on-message (apply handle-on-message event-args)
      nil)))

(defn run-chrome-event-loop! [chrome-event-channel]
  (prn "BACKGROUND: starting main event loop...")
  (go-loop [event-num 1]
    (when-some [event (<! chrome-event-channel)]
      (prn "event: " event) ;;xxx
      (process-chrome-event event-num event)
      (recur (inc event-num)))
    (prn "BACKGROUND: leaving main event loop")))

(defn boot-chrome-event-loop! []
  (let [chrome-event-channel (make-chrome-event-channel (chan))]
    ;; (tabs/tap-all-events chrome-event-channel)
    ;; (runtime/tap-all-events chrome-event-channel)
    (runtime/tap-on-message-events chrome-event-channel)
    (run-chrome-event-loop! chrome-event-channel)))

(defn process-message! [chan message]
  (log "CONTENT SCRIPT: got message:" message)
  )

; -- a simple page analysis  ------------------------------------------------------------------------------------------------


; -- main entry point -------------------------------------------------------------------------------------------------------

(defn init! []
  (let [_ (log "CONTENT SCRIPT: init")
        on-message (-> (js->clj js/chrome)
                       (get "runtime")
                       (get "onMessage"))
        ]

    (boot-chrome-event-loop!)
    (.addListener on-message (fn [message sender senderResponse]
                               ;; NO OP on purpose. process-chrome-event can handle it
                               (prn ">> on-message: " message)
                               ))
    ))
