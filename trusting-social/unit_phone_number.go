package main

import (
	"bufio"
	"errors"
	"fmt"
	"os"
	"sort"
	"strconv"
	"strings"
	"sync"
	"time"
)

const layout = "2006-01-02"

type Pair struct {
	First, Second int64
}

type Record struct {
	phone      int64
	activities []*Pair
}

func read(input *os.File) (map[int][]*Pair, error) {
	reader := bufio.NewReader(input)
	res := make(map[int][]*Pair)
	reader.ReadString('\n')
	for {
		text, ok := reader.ReadString('\n')
		if ok != nil {
			break
		}
		text = strings.TrimSuffix(text, "\n")
		s := strings.Split(text, ",")
		if len(s) != 3 {
			return nil, errors.New("wrong input format")
		}
		phone, err := strconv.Atoi(s[0])
		if err != nil {
			return nil, err
		}
		activate, err := time.Parse(layout, s[1])
		if err != nil {
			return nil, err
		}
		deactivate, err := time.Parse(layout, s[2])
		if err != nil {
			deactivate = time.Unix(0, 0)
		}
		res[phone] = append(res[phone], &Pair{
			First:  activate.Unix(),
			Second: deactivate.Unix(),
		})
	}
	return res, nil
}

func processPhoneActivities(in chan *Record, wg *sync.WaitGroup, output *os.File) {
	defer wg.Done()
	writer := bufio.NewWriter(output)
	for {
		record, ok := <-in
		if !ok {
			break
		}
		activities := record.activities
		sort.SliceStable(activities, func(i, j int) bool {
			return activities[i].First < activities[j].First
		})
		n := len(activities)
		activateDate := activities[n-1].First
		for i := n - 2; i >= 0; i-- {
			acti := activities[i].First
			deacti := activities[i].Second
			if activateDate == deacti {
				activateDate = acti
			} else {
				break
			}
		}
		activate := time.Unix(activateDate, 0)
		row := fmt.Sprintf("0%v,%v\n", record.phone, activate.Format(layout))
		writer.WriteString(row)
		writer.Flush()
	}
}

// exported method
func UnitPhoneNumberImp(numWorker int, intput *os.File, output *os.File) error {
	var wg sync.WaitGroup
	activitiesLog, err := read(intput)
	if err != nil {
		return err
	}
	n := len(activitiesLog)
	if numWorker > n {
		numWorker = n
	}
	channels := make([]chan *Record, numWorker)
	for i := range channels {
		// (n-1)/numWorker+1 mean buffer chan get ceil (round up)
		channels[i] = make(chan *Record, (n-1)/numWorker+1)
	}
	count := 0
	for phone, activities := range activitiesLog {
		channels[count%numWorker] <- &Record{
			phone:      int64(phone),
			activities: activities,
		}
		count++
	}
	writer := bufio.NewWriter(output)
	writer.WriteString("PHONE_NUMBER,REAL_ACTIVATION_DATE\n")
	writer.Flush()
	for i := 0; i < numWorker; i++ {
		wg.Add(1)
		go processPhoneActivities(channels[i], &wg, output)
	}
	for i := range channels {
		close(channels[i])
	}
	wg.Wait()
	return nil
}

func main() {
	args := os.Args
	var numWorker int = 3
	if len(args) == 2 {
		var err error = nil
		numWorker, err = strconv.Atoi(args[1])
		if err != nil {
			panic("numWorker must be numberic")
		}
	}
	UnitPhoneNumberImp(numWorker, os.Stdin, os.Stdout)
}
