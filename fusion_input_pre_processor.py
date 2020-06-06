import argparse
import datetime

SEPARATOR = ","
TIMESTAMP_FORMAT = "%Y%m%d%H%M"


class InputPreProcessor:

    def __init__(self, first_type, second_type, time_window):
        self.first_type = first_type
        self.second_type = second_type
        self.time_window = time_window
        self.output = []
        self.first_type_events = {}
        self.second_type_events = {}
        self.buffers = {self.first_type: self.first_type_events, self.second_type: self.second_type_events}

    def find_all_pairs_in_window(self, first_type, second_type, time_window):
        pass

    def process_new_line(self, line):
        event_type = self.get_type_in_line(line)
        if event_type in [self.first_type, self.second_type]:
            self.buffers[event_type][self.get_timestamp_in_line(line)] = self.get_line_attributes(line)
            if event_type == self.second_type:
                line = self.find_pairs_with_new_line(line)
                self.write_line_to_output(line)
        else:
            self.write_line_to_output(line)

    @staticmethod
    def split_line(line):
        return line.split(SEPARATOR)

    def get_type_in_line(self, line):
        return self.split_line(line)[0]

    def get_timestamp_in_line(self, line):
        return self.split_line(line)[1]

    def get_line_attributes(self, line):
        return SEPARATOR.join(self.split_line(line)[2:])[:-1]

    def find_pairs_with_new_line(self, line):
        new_pairs = []
        for (timestamp, attributes) in self.first_type_events.items():
            if self.validate_time_window(timestamp, self.get_timestamp_in_line(line)):
                new_pairs += self.fuse_events(line, attributes, timestamp)
        return new_pairs

    def get_opposite_event_type(self, event_type):
        return self.first_type if event_type == self.second_type else self.second_type

    def fuse_events(self, line, first_type_attributes, first_type_timestamp):
        return SEPARATOR.join([self.combine_event_names(), self.get_timestamp_in_line(line), first_type_attributes, self.get_line_attributes(line), first_type_timestamp]) + "\n"

    def combine_event_names(self):
        return self.first_type + self.second_type

    def write_line_to_output(self, line):
        self.output += line

    def validate_time_window(self, first_event_timestamp, second_event_timestamp):
        first_date_time = datetime.datetime.strptime(first_event_timestamp, TIMESTAMP_FORMAT)
        second_date_time = datetime.datetime.strptime(second_event_timestamp, TIMESTAMP_FORMAT)
        return first_date_time + datetime.timedelta(minutes=self.time_window) >= second_date_time


parser = argparse.ArgumentParser(description='This is a pre processor for simulating fusion as input')
parser.add_argument('file_path', help='The path of the input file')
parser.add_argument('first_type', help='The first type to fuse')
parser.add_argument('second_type', help='The second type to fuse')
parser.add_argument('time_window', type=int, help='The time window of the pattern')
parser.add_argument('output_file', help='Output path for the fused file')
args = parser.parse_args()

fusion_pre_processor = InputPreProcessor(args.first_type, args.second_type, args.time_window)
with open(args.file_path, "r") as input_file:
    for line in input_file.readlines():
        fusion_pre_processor.process_new_line(line)
with open(args.output_file, "w") as output_file:
    for line in fusion_pre_processor.output:
        output_file.write(line)
