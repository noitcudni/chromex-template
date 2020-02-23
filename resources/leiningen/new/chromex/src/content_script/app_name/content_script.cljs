(ns {{name}}.content-script
  (:require-macros [chromex.support :refer [runonce]])
  (:require [{{name}}.content-script.core :as core]))

(runonce
  (core/init!))
