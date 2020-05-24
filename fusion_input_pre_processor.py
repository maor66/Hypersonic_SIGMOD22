import argparse

SEPARATOR = ","


class InputPreProcessor:

    def __init__(self, first_type, second_type, time_window):
        self.first_type = first_type
        self.second_type = second_type
        self.time_window = time_window
        self.output = ""
        self.first_type_events = {}
        self.second_type_events = {}
        self.buffers = {self.first_type: self.first_type_events, self.second_type: self.second_type_events}

    def find_all_pairs_in_window(first_type, second_type, time_window):
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
        return self.split_line(line)[1:]

    def find_pairs_with_new_line(self, line):
        new_pairs = []
        for (timestamp, attributes) in self.second_type_events:
            if timestamp + self.time_window <= self.get_timestamp_in_line(line):  # found pair
                new_pairs += self.fuse_events(line, attributes)
        return new_pairs

    def get_opposite_event_type(self, event_type):
        return self.first_type if event_type == self.second_type else self.second_type

    def fuse_events(self, line, attributes):
        return self.combine_event_names() + self.get_timestamp_in_line(line) + self.get_line_attributes(
            line) + attributes

    def combine_event_names(self):
        return self.first_type + self.second_type

    def write_line_to_output(self, line):
        self.output += line


fusion_pre_processor = InputPreProcessor("GOOG", "MSFT", 3)
