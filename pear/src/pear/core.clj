(ns pear.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [digest]))

(def blockchain (atom []))
(def genesis-block {
  :nonce 0
  :hash "GENESIS"
})
(defn append-block
  "appends a block to the chain"
  [chain block]
  (swap! chain conj block))

(defn add-block 
  "This adds a block to the chain"
  [chain sender message]
  (let [hash (digest/md5 (json/write-str @chain))]
    (let [block {
        :nonce 0
        :message message 
        :sender sender
        :hash hash
      }]
      (append-block chain block))
  chain))

(defn validate
  "this validates the chain by checking the last block and
  recursively checking all before it"
  [chain]
  (println (count @chain))
  (if (> (count @chain) 1) ;; if this is not the first block
                           ;; check all previous blocks
    (let [new-chain (atom (drop-last @chain))]
      (let [last-block (last @chain)]
        (let [chain-hash (digest/md5 (json/write-str @new-chain))]
          (println "CHain Hash: " chain-hash)
          (println "Block Hash: " (:hash last-block))
          (if (= chain-hash (:hash last-block))
            (do
            (println "IF evaluated to true" (:hash last-block))
            (validate new-chain))
            (println "false!"))))))
  (if (= genesis-block
        (first @chain))
    (do
      (println "Checkin genesis blokk")
      true) 
    false)) ;; if it is first, return true

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (append-block blockchain genesis-block)
  (append-block blockchain {
    :nonce 234123123
    :hash "this is not a real blocc"
  })
  ;;(println (json/write-str
    ;;@(add-block blockchain "ried" "ur mum big bad dumb")))
  ;;(println @blockchain)
  (println "Blokk chain valid? " (validate blockchain)))