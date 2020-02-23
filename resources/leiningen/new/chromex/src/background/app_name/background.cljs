(ns {{name}}.background
  (:require-macros [chromex.support :refer [runonce]])
  (:require [{{name}}.background.core :as core]))

(runonce
  (core/init!))
