#!/usr/bin/env ruby

unless ARGV.length == 2
  puts "makeloud.rb <from> <to>"
  exit 1
end

from = ARGV[0]
to = ARGV[1]

path = File.join(Dir.pwd, to)

Dir.chdir(from) do
  Dir.glob('**/*.wav').each do |file|
    output_file = File.join(path, file)
 
    system "mkdir -p '#{File.dirname(output_file)}'"

    m = file.match(%r(.*L\.wav$))

    if m.nil?
      system "cp '#{file}' '#{output_file}'"
    else
      system "ffmpeg -i '#{file}' -af 'volume=10.0' '#{output_file}'"
    end
  end
end