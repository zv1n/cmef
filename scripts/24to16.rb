#!/usr/bin/env ruby

unless ARGV.length == 2
  puts "24to16.rb <from> <to>"
  exit 1
end

from = ARGV[0]
to = ARGV[1]
lcount = 0
scount = 0

path = File.join(Dir.pwd, to)

Dir.chdir(from) do
  Dir.glob('**/*.wav').each do |file|
    output_file = File.join(path, file)
    system "mkdir -p '#{File.dirname(output_file)}'"
    system "ffmpeg -i '#{file}' -acodec pcm_s16le -ar 44100 '#{output_file}'"
  end
end