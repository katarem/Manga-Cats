package io.github.katarem.application.mapper

import io.github.katarem.data.dto.ChapterDTO
import io.github.katarem.data.dto.MangaDTO
import io.github.katarem.data.dto.TagDTO
import io.github.katarem.data.entity.ChapterEntity
import io.github.katarem.data.entity.MangaEntity
import io.github.katarem.data.entity.TagEntity
import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga
import io.github.katarem.data.model.Tag
import tech.mappie.api.ObjectMappie

object MangaMappers {
    object MangaToEntity : ObjectMappie<Manga, MangaEntity>() {
        override fun map(from: Manga): MangaEntity = mapping {
            to::currentChapterIndex fromProperty from::currentChapterIndex
            to::description fromValue from.description.getOrElse(from.language, { "en" })
        }
    }

    object EntityToManga : ObjectMappie<MangaEntity, Manga>() {
        override fun map(from: MangaEntity): Manga = mapping {
            to::description fromValue mapOf(Pair(from.language, from.description))
        }
    }
    object DtoToManga : ObjectMappie<MangaDTO, Manga>() {
        override fun map(from: MangaDTO): Manga = mapping {
            to::title fromExpression {
                from.attributes.title.values.firstOrNull() ?: "No Title"
            }
            to::description fromExpression {
                from.attributes.description
            }
            to::coverArt fromExpression {
                val filename = from.relationships.firstOrNull { it.type == "cover_art" }?.attributes?.fileName
                val url = "https://uploads.mangadex.org/covers/${from.id}/$filename"
                return@fromExpression url
            }
            to::tags fromExpression {
                from.attributes.tags.map(TagMappers.DtoToModel::map)
            }
            to::language fromProperty from::language
        }
    }
}

object TagMappers {
    object DtoToModel: ObjectMappie<TagDTO, Tag>() {
        override fun map(from: TagDTO): Tag = mapping {
            to::name fromExpression {
                it.attributes.name.values.firstOrNull() ?: "No name"
            }
            to::group fromValue from.attributes.group
        }
    }
    object ModelToEntity: ObjectMappie<Tag, TagEntity>()
    object EntityToModel: ObjectMappie<TagEntity, Tag>()
}

object ChapterMappers {
    object ChapterToEntity : ObjectMappie<Chapter, ChapterEntity>()
    object EntityToChapter : ObjectMappie<ChapterEntity, Chapter>()
    object DtoToChapter : ObjectMappie<ChapterDTO, Chapter>() {
        override fun map(from: ChapterDTO): Chapter = mapping {
            to::mangaId fromExpression {
                from.relationships.firstOrNull { it.type == "manga" }?.id ?: ""
            }
            to::title fromExpression {
                from.attributes.title ?: "No Title"

            }
            to::chapterNumber fromExpression {
                from.attributes.chapter ?: "No Number"
            }
            to::offline fromValue false
        }
    }
}