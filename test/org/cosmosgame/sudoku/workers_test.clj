(ns org.cosmosgame.sudoku.workers-test
  (:use clojure.test
        org.cosmosgame.sudoku.core
        org.cosmosgame.sudoku.workers
        org.cosmosgame.sudoku.workers-test
        ))

  (def test-queue (queue))
  (def test-result (promise))
  (defn test-pred
    [q x]
    (if (= x "done")
      (deliver (:result q) "done!")))

(deftest test-offer
  (let [q (queue)
        _ (offer q "test")]
    (is (= ["test"] (into [] (:lbq q)) ))))

(deftest test-offer-all
  (let [q (queue)
        expected ["one" "two" "three"]
        _ (offer-all q expected)]
    (is (= expected (into [] (:lbq q)) ))))

(deftest test-start-workers
  (let [q (queue 3)
        workers (start-workers q test-pred)]
    (is (= 3 (count workers)))
    (is (every? #(not (realized? %)) workers ))
    (offer q "done")
    (Thread/sleep 100)
    (is (realized? (:result q) ))
    (is (= "done!" @(:result q)))
    (is (= @(:idle-workers q) (:num-workers q))) ))

(deftest test-stop
  (let [q (queue 3)
        workers (start-workers q test-pred)]
    ; wait here until we time out
    (is (nil? @(:result q)))
    (is (= 3 @(:idle-workers q))))

  (let [q (queue 3)
      workers (start-workers q test-pred)]
    (offer q "one")
    (Thread/sleep 100)
    (is (= (not (realized? (:result q)))))
    (offer q "two")

    ; wait here until we time out
    (is (nil? @(:result q)))))

;http://norvig.com/sudoku.html
; This one takes a long time when we don't use multiple processes
; It turns out there are multiple correct answers

(deftest test-psolve
  (testing "testing hard board..."
    (let [ board (read-board"000006000059000008200008000045000000003000000006003054000325006000000000000000000")
           result (psolve board)]
      (is (not (nil? result)))
      (is (valid-answer? result)) )))



