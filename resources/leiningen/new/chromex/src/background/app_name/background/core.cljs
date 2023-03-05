(ns bulk-slack-user-deactivation.background.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [goog.string :as gstring]
            [goog.string.format]
            [cljs.core.async :refer [<! chan]]
            [chromex.logging :refer-macros [log]]
            [chromex.chrome-event-channel :refer [make-chrome-event-channel]]
            [chromex.protocols.chrome-port :refer [post-message! get-sender]]
            [chromex.ext.tabs :as tabs]
            [chromex.ext.runtime :as runtime]
            ;; [bulk-slack-user-deactivation.background.storage :refer [test-storage!]]
            ))

;; ; -- event handlers ---------------------------------------------------------------------------------------------------------
(defn handle-on-install [event-args]
  (when (= "install" (get (js->clj event-args) "reason"))
    ;; tabs/create returns a channel, but I don't think anything needs to be done with the result
    (tabs/create (clj->js {:url "https://news.ycombinator.com/" :active true}))))

(defn handle-on-message [message]
  (go (let [t (<! (tabs/query #js{:active true :currentWindow true}))
            tab-id (-> t js->clj ffirst (get "id"))
            _ (prn ">> handle-on-message : " message)
            ]
        )))

;; ; -- main event loop --------------------------------------------------------------------------------------------------------

(defn process-chrome-event [event-num event]
  (log (gstring/format "BACKGROUND: got chrome event (%05d)" event-num) event)
  (let [[event-id event-args] event]
    (case event-id
      ::runtime/on-installed (apply handle-on-install event-args)
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
    (run-chrome-event-loop! chrome-event-channel)
    ))

; -- main entry point -------------------------------------------------------------------------------------------------------

(defn init! []
  (log "BACKGROUND: init")
  (js/console.log "am I here?")
  (boot-chrome-event-loop!)
  )
