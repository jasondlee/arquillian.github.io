bundle install
bundle info bootstrap
bundle exec jekyll serve \
    --incremental \
    -H 0.0.0.0 \
    -l \
    -w \
    --force_polling \
    --trace
